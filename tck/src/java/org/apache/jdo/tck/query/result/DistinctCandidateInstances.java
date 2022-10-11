/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package org.apache.jdo.tck.query.result;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.jdo.tck.JDO_Test;
import org.apache.jdo.tck.pc.company.CompanyModelReader;
import org.apache.jdo.tck.pc.company.Employee;
import org.apache.jdo.tck.pc.company.Person;
import org.apache.jdo.tck.query.QueryElementHolder;
import org.apache.jdo.tck.query.QueryTest;
import org.apache.jdo.tck.util.BatchTestRunner;

/**
 *<B>Title:</B> Distinct Candidate Instances.
 *<BR>
 *<B>Keywords:</B> query
 *<BR>
 *<B>Assertion ID:</B> A14.6.9-2.
 *<BR>
 *<B>Assertion Description: </B>
 * Queries against an extent always consider only distinct candidate instances,
 * regardless of whether distinct is specified. 
 * Queries against a collection might contain duplicate candidate instances; 
 * the distinct keyword removes duplicates from the candidate collection 
 * in this case.
 */
public class DistinctCandidateInstances extends QueryTest {

    /** */
    private static final String ASSERTION_FAILED = 
        "Assertion A14.6.9-2 (DistintCandidateInstances) failed: ";
    
    /** 
     * The array of valid queries which may be executed as 
     * single string queries and as API queries.
     */
    private static final QueryElementHolder[] VALID_QUERIES = {
        new QueryElementHolder(
        /*UNIQUE*/      null,
        /*RESULT*/      null,
        /*INTO*/        null, 
        /*FROM*/        Employee.class,
        /*EXCLUDE*/     null,
        /*WHERE*/       null,
        /*VARIABLES*/   "Project p",
        /*PARAMETERS*/  null,
        /*IMPORTS*/     null,
        /*GROUP BY*/    null,
        /*ORDER BY*/    null,
        /*FROM*/        null,
        /*TO*/          null),
        new QueryElementHolder(
        /*UNIQUE*/      null,
        /*RESULT*/      "DISTINCT",
        /*INTO*/        null, 
        /*FROM*/        Employee.class,
        /*EXCLUDE*/     null,
        /*WHERE*/       null,
        /*VARIABLES*/   "Project p",
        /*PARAMETERS*/  null,
        /*IMPORTS*/     null,
        /*GROUP BY*/    null,
        /*ORDER BY*/    null,
        /*FROM*/        null,
        /*TO*/          null)
    };
    
    /** 
     * The expected results of valid queries.
     */
    private Object[] expectedResult = {
        getTransientCompanyModelInstancesAsList(new String[]{"emp1", "emp1"}),
        getTransientCompanyModelInstancesAsList(new String[]{"emp1"})
    };
            
    /**
     * The <code>main</code> is called when the class
     * is directly executed from the command line.
     * @param args The arguments passed to the program.
     */
    public static void main(String[] args) {
        BatchTestRunner.run(DistinctCandidateInstances.class);
    }
    
    /** */
    public void testExtentQueries() {
        if (isUnconstrainedVariablesSupported()) {
            for (int i = 0; i < VALID_QUERIES.length; i++) {
                executeAPIQuery(ASSERTION_FAILED, VALID_QUERIES[i], 
                        expectedResult[i]);
                executeSingleStringQuery(ASSERTION_FAILED, VALID_QUERIES[i], 
                        expectedResult[i]);
            }
        }
    }
     
    /** */
    public void testCollectionQueries() {
        String singleStringQuery = "SELECT FROM " + Person.class.getName();
        String singleStringDistinctQuery = 
            "SELECT DISTINCT FROM " + Person.class.getName();
        
        List candidates = getPersistentCompanyModelInstancesAsList(
            new String[]{"emp1", "emp1"});
        Query query = pm.newQuery();
        query.setClass(Person.class);
        query.setCandidates(candidates);
        query.setResult("this");
        executeJDOQuery(ASSERTION_FAILED, query, singleStringQuery, 
                false, null, expectedResult[0], true);
        
        query.setResult("DISTINCT this");
        executeJDOQuery(ASSERTION_FAILED, query, singleStringDistinctQuery, 
                false, null, expectedResult[1], true);
    }

    /**
     * @see JDO_Test#localSetUp()
     */
    protected void localSetUp() {
        addTearDownClass(CompanyModelReader.getTearDownClasses());
        loadAndPersistCompanyModel(getPM());
    }
    
}
