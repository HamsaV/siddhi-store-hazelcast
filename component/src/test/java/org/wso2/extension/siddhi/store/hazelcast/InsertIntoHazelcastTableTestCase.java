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

import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.stream.input.InputHandler;

//import org.testng.annotations.Test;

public class InsertIntoHazelcastTableTestCase {

    private static final Logger log = Logger.getLogger(InsertIntoHazelcastTableTestCase.class);


    @Test(testName = "hazelcasttableinsertiontest1")
    public void hbasetableinsertiontest1() throws InterruptedException {
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" + "define stream StockStream (symbol string, volume long); "
                + "@Store(type='hazelcast', cluster.name='dev', cluster.password='dev-pass', "
                + "cluster.addresses='172.17.0.1:5701', collection.name ='insert-test')"
                + "define table StockTable (symbol string, volume long); ";

        String query = "" + "@info(name = 'query1') " + "from StockStream\n" + "select symbol, volume\n"
                + "insert into StockTable ;";

        log.info(streams + query);

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("StockStream");
        siddhiAppRuntime.start();

        stockStream.send(new Object[] { "WSO2", 100L });
        stockStream.send(new Object[] { "IBM", 100L });
       // stockStream.send(new Object[] { "MSFT", 57.6F, 100L });
        Thread.sleep(1000);
        siddhiAppRuntime.shutdown();
    }

   // @Test(testName = "hazelcasttableinsertiontest2")
    public void hbasetableinsertiontest2() throws InterruptedException {
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" + "define stream StockStream (symbol string, price float, volume long); "
                + "@Store(type=\"hazelcast\", cluster.name='dev', cluster.password='dev-pass', "
                + "cluster.addresses='172.17.0.1:5701')"
                + "define table StockTable (symbol string, price float, volume long); ";

        String query = "" + "@info(name = 'query1') " + "from StockStream   " + "insert into StockTable ;";

        log.info(streams + query);

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("StockStream");
        siddhiAppRuntime.start();

        stockStream.send(new Object[] { "WSO2", 55.6F, 100L });
        stockStream.send(new Object[] { "IBM", 75.6F, 100L });
        stockStream.send(new Object[] { "MSFT", 57.6F, 100L });
        Thread.sleep(1000);
        siddhiAppRuntime.shutdown();
    }

}
