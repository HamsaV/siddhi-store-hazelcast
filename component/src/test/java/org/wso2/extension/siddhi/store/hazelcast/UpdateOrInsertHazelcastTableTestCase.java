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

import java.sql.SQLException;

public class UpdateOrInsertHazelcastTableTestCase {
    private static final Logger log = Logger.getLogger(UpdateOrInsertHazelcastTableTestCase.class);

    @Test
    public void updateOrInsertTableTest1() throws InterruptedException, SQLException {
        log.info("updateOrInsertTableTest1");
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" +
                "define stream StockStream (symbol string, price float, volume long); " +
                "define stream UpdateStockStream (symbol string, price float, volume long); "
                + "@Store(type=\"hazelcast\", cluster.name='dev', cluster.password='dev-pass', "
                + "cluster.addresses='172.17.0.1:5701',collection.name ='upOrIns-test')"
                + "define table StockTable (symbol string, price float, volume long); ";
        String query = "" + "@info(name = 'query1') " + "from StockStream " + "insert into StockTable ;" + ""
                + "@info(name = 'query2') " + "from UpdateStockStream#window.timeBatch(1 sec) "
                + "update or insert into StockTable " + "   on StockTable.symbol=='IM' and StockTable.symbol=='GO';";

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("StockStream");
        InputHandler updateStockStream = siddhiAppRuntime.getInputHandler("UpdateStockStream");
        siddhiAppRuntime.start();

        stockStream.send(new Object[] { "WSO2", 100L });
        stockStream.send(new Object[] { "IM", 100L });
       // stockStream.send(new Object[] { "WSO2", 100L });
        updateStockStream.send(new Object[] { "GO", 100L });
        Thread.sleep(3000);

        siddhiAppRuntime.shutdown();
    }
}
