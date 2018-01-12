package org.wso2.extension.siddhi.store.hazelcast;
/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.stream.input.InputHandler;

public class DeleteFromHazelcastTestCase {
    private static final Log log = LogFactory.getLog(DeleteFromHazelcastTestCase.class);

    @Test(description = "deleteFromHazelcastTableTest1")
    public void deleteFromHBaseTableTest1() throws InterruptedException {
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" + "define stream StockStream (symbol string,  volume long); "
                + "define stream DeleteStockStream (symbol string, price float, volume long); "
                + "@Store(type=\"hazelcast\", cluster.name='dev', cluster.password='dev-pass', "
                + "cluster.addresses='172.17.0.1:5701',collection.name ='test')"
                + "define table StockTable (symbol string, volume long); ";
        String query = "" + "@info(name = 'query1') " + "from StockStream " + "insert into StockTable ;" + ""
                + "@info(name = 'query2') " + "from DeleteStockStream " + "delete StockTable "
                + "   on StockTable.symbol == symbol ;";

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("StockStream");
        InputHandler deleteStockStream = siddhiAppRuntime.getInputHandler("DeleteStockStream");
        siddhiAppRuntime.start();

        stockStream.send(new Object[] { "WSO",  100L });
        deleteStockStream.send(new Object[] { "WSO", 100L });
        stockStream.send(new Object[] { "WSO",  100L });
        Thread.sleep(1000);
        siddhiAppRuntime.shutdown();
    }
}
