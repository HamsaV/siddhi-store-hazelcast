/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.extension.siddhi.store.hazelcast.util;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.HazelcastInstance;
import org.apache.log4j.Logger;
import org.wso2.siddhi.annotation.Example;
import org.wso2.siddhi.annotation.Extension;
import org.wso2.siddhi.annotation.Parameter;
import org.wso2.siddhi.annotation.util.DataType;
import org.wso2.siddhi.core.exception.ConnectionUnavailableException;
import org.wso2.siddhi.core.table.record.AbstractRecordTable;
import org.wso2.siddhi.core.table.record.ExpressionBuilder;
import org.wso2.siddhi.core.table.record.RecordIterator;
import org.wso2.siddhi.core.util.SiddhiConstants;
import org.wso2.siddhi.core.util.collection.operator.CompiledCondition;
import org.wso2.siddhi.core.util.collection.operator.CompiledExpression;
import org.wso2.siddhi.core.util.config.ConfigReader;
import org.wso2.siddhi.query.api.annotation.Annotation;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.definition.TableDefinition;
import org.wso2.siddhi.query.api.util.AnnotationHelper;

import java.util.List;
import java.util.Map;

import static org.wso2.extension.siddhi.store.hazelcast.util.HazelcastEventTableConstants.ANNOTATION_ELEMENT_HAZELCAST_CLUSTER_ADDRESSES;
import static org.wso2.extension.siddhi.store.hazelcast.util.HazelcastEventTableConstants.ANNOTATION_ELEMENT_HAZELCAST_CLUSTER_COLLECTION;
import static org.wso2.extension.siddhi.store.hazelcast.util.HazelcastEventTableConstants.ANNOTATION_ELEMENT_HAZELCAST_CLUSTER_NAME;
import static org.wso2.extension.siddhi.store.hazelcast.util.HazelcastEventTableConstants.ANNOTATION_ELEMENT_HAZELCAST_CLUSTER_PASSWORD;
import static org.wso2.siddhi.core.util.SiddhiConstants.ANNOTATION_STORE;

/**
 * Hazelcast event table implementation.
 */
@Extension(name = "hazelcast",
           namespace = "store",
           description = " ",
           parameters = {
                   @Parameter(name = "cluster.name",
                              description = "Hazelcast cluster name",
                              optional = true,
                              defaultValue = "dev",
                              type = { DataType.STRING }),
                   @Parameter(name = "cluster.password",
                              description = "Hazelcast cluster password ",
                              optional = true,
                              defaultValue = "dev-pass",
                              type = { DataType.STRING }),
                   @Parameter(name = "collection.name",
                              description = "Name of the collection ",
                              optional = true,
                              defaultValue = "Name of the siddhi event table.",
                              type = { DataType.STRING }),
                   @Parameter(name = "cluster.addresses",
                              description = "Hazelcast cluster address ",
                              type = { DataType.STRING }),
           },
           examples = {
                   @Example(syntax = "define stream StockStream (symbol string, price float, volume long); "
                           + "@Store(type=\"hazelcast\", cluster.name='dev', cluster.password='dev-pass', "
                           + "cluster.addresses='172.17.0.1:5701',collection.name='collection-name')"
                           + "define table StockTable (symbol string, price float, volume long);",
                            description = "This definition creates an event table named `StockTable "
                                    + " on the Hazelcast instance The connection is made as specified by the "
                                    + "parameters configured for the '@Store' annotation. The `symbol` attribute is "
                                    + "considered a unique field")
           })

public class HazelcastEventTable extends AbstractRecordTable {
    private static final Logger log = Logger.getLogger(HazelcastEventTable.class);
    private List<Attribute> attributes;
    private Annotation storeAnnotation;
    private Annotation primaryKeys;
    private String clusterName;
    private String clusterPassword;
    private String hosts;
    private String collectionName;

    private HazelcastInstance client;

    @Override
    protected void init(TableDefinition tableDefinition, ConfigReader configReader) {
        attributes = tableDefinition.getAttributeList();
        storeAnnotation = AnnotationHelper.getAnnotation(ANNOTATION_STORE, tableDefinition.getAnnotations());
        primaryKeys = AnnotationHelper
                .getAnnotation(SiddhiConstants.ANNOTATION_PRIMARY_KEY, tableDefinition.getAnnotations());
        clusterName = storeAnnotation.getElement(ANNOTATION_ELEMENT_HAZELCAST_CLUSTER_NAME);
        clusterPassword = storeAnnotation.getElement(ANNOTATION_ELEMENT_HAZELCAST_CLUSTER_PASSWORD);
        hosts = storeAnnotation.getElement(ANNOTATION_ELEMENT_HAZELCAST_CLUSTER_ADDRESSES);
        collectionName = storeAnnotation.getElement(ANNOTATION_ELEMENT_HAZELCAST_CLUSTER_COLLECTION);

    }

    @Override
    protected void add(List<Object[]> list) throws ConnectionUnavailableException {
        this.insertRecords(String.valueOf(list.get(0)[0]), String.valueOf(list.get(0)[1]));

    }

    public void insertRecords(String key, String value) {
        try {
            HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance();
            Map<String, String> map = hzInstance.getMap(collectionName);

            if (!map.containsKey(key)) {
                map.put(key, value);
            } else {
                log.error("The record is already exists");
            }
        } catch (Exception e) {
            throw new HazelcastException("Error occurs when insert operation happens" + e.getMessage(), e);
        }

    }

    public void deleteRecords(String key) {
        try {
            HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance();
            Map<String, String> map = hzInstance.getMap(collectionName);
            map.remove(key);
        } catch (Exception e) {
            throw new HazelcastException("Error occurs when delete operation happens" + e.getMessage(), e);
        }

    }

    public void updateRecords(String key, String value) {
        try {
            HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance();
            Map<String, String> map = hzInstance.getMap(collectionName);
            if (map.containsKey(key)) {
                map.put(key, value);
            } else if (!map.containsKey(key)) {
                log.info("Unable to do the update operation,since the record you wants to update is not exist");
            }
        } catch (Exception e) {
            throw new HazelcastException("Error occurs when update operation happens" + e.getMessage(), e);
        }

    }

    public boolean containsRecords(String key) {
        try {
            HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance();
            Map<String, String> map = hzInstance.getMap(collectionName);
            if (map.containsKey(key)) {
                return true;
            } else {
                log.info("The record you want to check is not exist in the collection");
                return false;
            }
        } catch (Exception e) {
            throw new HazelcastException("Error occurs when check the given record" + e.getMessage(), e);
        }

    }

    public void upsertRecords(String key, String value) {
        try {
            HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance();
            Map<String, String> map = hzInstance.getMap(collectionName);
            map.put(key, value);
        } catch (Exception e) {
            throw new HazelcastException(
                    "Error occurs when performing update/insert operation happens" + e.getMessage(), e);
        }

    }

    @Override
    protected RecordIterator<Object[]> find(Map<String, Object> map, CompiledCondition compiledCondition)
            throws ConnectionUnavailableException {
        return null;
    }

    @Override
    protected boolean contains(Map<String, Object> map, CompiledCondition compiledCondition)
            throws ConnectionUnavailableException {
        return false;
    }

    @Override
    protected void delete(List<Map<String, Object>> list, CompiledCondition compiledCondition)
            throws ConnectionUnavailableException {
        Map<String, Object> map = list.get(0);
        String[] s = list.get(0).keySet().toArray(new String[0]);
        this.deleteRecords(String.valueOf(map.get(s[0])));

    }

    @Override
    protected void update(CompiledCondition compiledCondition, List<Map<String, Object>> updateConditionParameterMaps,
            Map<String, CompiledExpression> updateSetExpressions, List<Map<String, Object>> updateValues)
            throws ConnectionUnavailableException {
        this.updateRecords(String.valueOf(updateValues.get(0)), String.valueOf(updateValues.get(1)));

    }

    @Override
    protected void updateOrAdd(CompiledCondition compiledCondition, List<Map<String, Object>> list,
            Map<String, CompiledExpression> map, List<Map<String, Object>> list1, List<Object[]> list2)
            throws ConnectionUnavailableException {
        this.upsertRecords(list.get(0).toString(), list.get(1).toString());

    }

    @Override
    protected CompiledCondition compileCondition(ExpressionBuilder expressionBuilder) {
        HazelcastExpressionVisitor visitor = new HazelcastExpressionVisitor();
        expressionBuilder.build(visitor);
        return new HazelcastCompiledCondition(visitor.returnCondition());
    }

    @Override
    protected CompiledExpression compileSetAttribute(ExpressionBuilder expressionBuilder) {
        return compileCondition(expressionBuilder);
    }

    @Override
    protected void connect() throws ConnectionUnavailableException {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getGroupConfig().setName(clusterName).setPassword(clusterPassword);
        clientConfig.getNetworkConfig().addAddress(hosts);
        client = HazelcastClient.newHazelcastClient(clientConfig);

    }

    @Override
    protected void disconnect() {
        client.shutdown();

    }

    @Override
    protected void destroy() {

    }
}
