/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gitee.uidhxd.drools7.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTDRLPersistence;
import org.drools.workbench.models.guided.dtable.backend.util.DataUtilities;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DescriptionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.junit.Test;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.oracle.DataType;

public class GuidedDTDRLPersistenceTest {

    @Test
    public void testInWithSimpleSingleLiteralValue() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableName("in_operator");

        Pattern52 p1 = new Pattern52();
        p1.setFactType("Person");

        ConditionCol52 con = new ConditionCol52();
        con.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        con.setFieldType(DataType.TYPE_STRING);
        con.setFactField("field1");
        con.setHeader("Person field1");
        con.setOperator("in");
        p1.getChildColumns().add(con);

        dt.getConditions().add(p1);

        dt.setData(DataUtilities.makeDataLists(new String[][]{
                new String[]{"1", "desc1", "ak1,mk1"},
                new String[]{"2", "desc2", "(ak2,mk2)"},
                new String[]{"3", "desc3", "( ak3, mk3 )"},
                new String[]{"4", "desc4", "( \"ak4\", \"mk4\" )"},
                new String[]{"5", "desc5", "( \"ak5 \", \" mk5\" )"},
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);
        System.out.println(drl);
        
        String expected = "//from row number: 1\n" +
                "//desc1\n" +
                "rule \"Row 1 in_operator\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  Person( field1 in ( \"ak1\", \"mk1\" ) )\n" +
                "then\n" +
                "end\n" +
                "//from row number: 2\n" +
                "//desc2\n" +
                "rule \"Row 2 in_operator\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  Person( field1 in ( \"ak2\", \"mk2\" ) )\n" +
                "then\n" +
                "end\n" +
                "//from row number: 3\n" +
                "//desc3\n" +
                "rule \"Row 3 in_operator\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  Person( field1 in ( \"ak3\", \"mk3\" ) )\n" +
                "then\n" +
                "end\n" +
                "//from row number: 4\n" +
                "//desc4\n" +
                "rule \"Row 4 in_operator\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  Person( field1 in ( \"ak4\", \"mk4\" ) )\n" +
                "then\n" +
                "end\n" +
                "//from row number: 5\n" +
                "//desc5\n" +
                "rule \"Row 5 in_operator\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  Person( field1 in ( \"ak5 \", \" mk5\" ) )\n" +
                "then\n" +
                "end";

        assertEqualsIgnoreWhitespace(expected,
                                     drl);
    }

    @Test
    public void test2Rules() throws Exception {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        dt.setTableName("michael");

        AttributeCol52 attr = new AttributeCol52();
        attr.setAttribute("salience");
        attr.setDefaultValue(new DTCellValue52("66"));
        dt.getAttributeCols().add(attr);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("f1");
        p1.setFactType("Driver");

        ConditionCol52 con = new ConditionCol52();
        con.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        con.setFactField("age");
        con.setHeader("Driver f1 age");
        con.setOperator("==");
        p1.getChildColumns().add(con);

        ConditionCol52 con2 = new ConditionCol52();
        con2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        con2.setFactField("name");
        con2.setHeader("Driver f1 name");
        con2.setOperator("==");
        p1.getChildColumns().add(con2);

        ConditionCol52 con3 = new ConditionCol52();
        con3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_RET_VALUE);
        con3.setFactField("rating");
        con3.setHeader("Driver rating");
        con3.setOperator("==");
        p1.getChildColumns().add(con3);

        dt.getConditions().add(p1);

        Pattern52 p2 = new Pattern52();
        p2.setBoundName("f2");
        p2.setFactType("Driver");

        ConditionCol52 con4 = new ConditionCol52();
        con4.setConstraintValueType(BaseSingleFieldConstraint.TYPE_PREDICATE);
        con4.setHeader("Driver 2 pimp");
        con4.setFactField("(not needed)");
        p2.getChildColumns().add(con4);

        dt.getConditions().add(p2);

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName("ins");
        ins.setFactType("Cheese");
        ins.setFactField("price");
        ins.setType(DataType.TYPE_NUMERIC_INTEGER);
        dt.getActionCols().add(ins);

        ActionRetractFactCol52 ret = new ActionRetractFactCol52();
        dt.getActionCols().add(ret);

        ActionSetFieldCol52 set = new ActionSetFieldCol52();
        set.setBoundName("f1");
        set.setFactField("goo1");
        set.setType(DataType.TYPE_STRING);
        dt.getActionCols().add(set);

        ActionSetFieldCol52 set2 = new ActionSetFieldCol52();
        set2.setBoundName("f1");
        set2.setFactField("goo2");
        set2.setDefaultValue(new DTCellValue52("whee"));
        set2.setType(DataType.TYPE_STRING);
        dt.getActionCols().add(set2);

        dt.setData(DataUtilities.makeDataLists(new String[][]{
                new String[]{"1", "desc", "42", "33", "michael", "age * 0.2", "age > 7", "6.60", "true", "gooVal1", "f2"},
                new String[]{"2", "desc", "66", "39", "bob", "age * 0.3", "age > 7", "6.60", "", "gooVal1", "whee"}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        System.out.println(drl);
    }


    @Test
    public void testInOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableName("michael");

        AttributeCol52 attr = new AttributeCol52();
        attr.setAttribute("salience");
        attr.setDefaultValue(new DTCellValue52("66"));
        dt.getAttributeCols().add(attr);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("f1");
        p1.setFactType("Driver");

        ConditionCol52 con = new ConditionCol52();
        con.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        con.setFactField("age");
        con.setHeader("Driver f1 age");
        con.setOperator("==");
        p1.getChildColumns().add(con);

        ConditionCol52 con2 = new ConditionCol52();
        con2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        con2.setFactField("name");
        con2.setHeader("Driver f1 name");
        con2.setOperator("in");
        p1.getChildColumns().add(con2);

        ConditionCol52 con3 = new ConditionCol52();
        con3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_RET_VALUE);
        con3.setFactField("rating");
        con3.setHeader("Driver rating");
        con3.setOperator("==");
        p1.getChildColumns().add(con3);

        ConditionCol52 con4 = new ConditionCol52();
        con4.setConstraintValueType(BaseSingleFieldConstraint.TYPE_PREDICATE);
        con4.setHeader("Driver 2 pimp");
        con4.setFactField("(not needed)");
        p1.getChildColumns().add(con4);

        dt.getConditions().add(p1);

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName("ins");
        ins.setFactType("Cheese");
        ins.setFactField("price");
        ins.setType(DataType.TYPE_NUMERIC_INTEGER);
        dt.getActionCols().add(ins);

        ActionRetractFactCol52 ret = new ActionRetractFactCol52();
        dt.getActionCols().add(ret);

        ActionSetFieldCol52 set = new ActionSetFieldCol52();
        set.setBoundName("f1");
        set.setFactField("goo1");
        set.setType(DataType.TYPE_STRING);
        dt.getActionCols().add(set);

        ActionSetFieldCol52 set2 = new ActionSetFieldCol52();
        set2.setBoundName("f1");
        set2.setFactField("goo2");
        set2.setDefaultValue(new DTCellValue52("whee"));
        set2.setType(DataType.TYPE_STRING);
        dt.getActionCols().add(set2);

        dt.setData(DataUtilities.makeDataLists(new String[][]{
                new String[]{"1", "desc", "42", "33", "michael, manik", "age * 0.2", "age > 7", "6.60", "true", "gooVal1", "f2"},
                new String[]{"2", "desc", "", "39", "bob, frank", "age * 0.3", "age > 7", "6.60", "", "gooVal1", null}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        System.out.println(drl);
    }

    @Test
    public void testInterpolate() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableName("michael");

        AttributeCol52 attr = new AttributeCol52();
        attr.setAttribute("salience");
        attr.setDefaultValue(new DTCellValue52("66"));
        dt.getAttributeCols().add(attr);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("f1");
        p1.setFactType("Driver");

        ConditionCol52 con = new ConditionCol52();
        con.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        con.setFactField("age");
        con.setHeader("Driver f1 age");
        con.setOperator("==");
        p1.getChildColumns().add(con);

        ConditionCol52 con2 = new ConditionCol52();
        con2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        con2.setFactField("name");
        con2.setHeader("Driver f1 name");
        con2.setOperator("==");
        p1.getChildColumns().add(con2);

        ConditionCol52 con3 = new ConditionCol52();
        con3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_RET_VALUE);
        con3.setFactField("rating");
        con3.setHeader("Driver rating");
        con3.setOperator("==");
        p1.getChildColumns().add(con3);

        dt.getConditions().add(p1);

        Pattern52 p2 = new Pattern52();
        p2.setBoundName("f2");
        p2.setFactType("Driver");

        ConditionCol52 con4 = new ConditionCol52();
        con4.setConstraintValueType(BaseSingleFieldConstraint.TYPE_PREDICATE);
        con4.setHeader("Driver 2 pimp");
        con4.setFactField("this.hasSomething($param)");
        p2.getChildColumns().add(con4);

        dt.getConditions().add(p2);

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName("ins");
        ins.setFactType("Cheese");
        ins.setFactField("price");
        ins.setType(DataType.TYPE_NUMERIC_INTEGER);
        dt.getActionCols().add(ins);

        ActionRetractFactCol52 ret = new ActionRetractFactCol52();
        dt.getActionCols().add(ret);

        ActionSetFieldCol52 set = new ActionSetFieldCol52();
        set.setBoundName("f1");
        set.setFactField("goo1");
        set.setType(DataType.TYPE_STRING);
        dt.getActionCols().add(set);

        ActionSetFieldCol52 set2 = new ActionSetFieldCol52();
        set2.setBoundName("f1");
        set2.setFactField("goo2");
        set2.setDefaultValue(new DTCellValue52("whee"));
        set2.setType(DataType.TYPE_STRING);
        dt.getActionCols().add(set2);

        dt.setData(DataUtilities.makeDataLists(new String[][]{
                new String[]{"1", "desc", "42", "33", "michael", "age * 0.2", "BAM", "6.60", "true", "gooVal1", "f2"},
                new String[]{"2", "desc", "66", "39", "bob", "age * 0.3", "BAM", "6.60", "", "gooVal1", "whee"}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        System.out.println(drl);
    }


    @Test
    public void testNoConstraints() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
        dt.setTableName("no-constraints");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("x");
        p1.setFactType("Context");

        ConditionCol52 c = new ConditionCol52();
        c.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c.setFieldType(DataType.TYPE_STRING);
        c.setFactField("name");
        c.setOperator("==");
        p1.getChildColumns().add(c);

        dt.getConditions().add(p1);

        ActionSetFieldCol52 asf = new ActionSetFieldCol52();
        asf.setBoundName("x");
        asf.setFactField("age");
        asf.setType(DataType.TYPE_NUMERIC_INTEGER);

        dt.getActionCols().add(asf);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{"1", "desc", "Fred", 75l}
        }));

        String drl1 = GuidedDTDRLPersistence.getInstance().marshal(dt);
        String expected1 = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row 1 no-constraints\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    x : Context( name == \"Fred\" )\n" +
                "  then\n" +
                "    x.setAge( 75 );\n" +
                "end";

        assertEqualsIgnoreWhitespace(expected1,
                                     drl1);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{"1", "desc", null, 75l}
        }));

        String drl2 = GuidedDTDRLPersistence.getInstance().marshal(dt);
        String expected2 = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row 1 no-constraints\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "    x.setAge( 75 );\n" +
                "end";

        assertEqualsIgnoreWhitespace(expected2,
                                     drl2);
    }


    @Test
    public void testUpdateModifySingleField() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("x");
        p1.setFactType("Context");

        ConditionCol52 c = new ConditionCol52();
        c.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c);
        dt.getConditions().add(p1);

        ActionSetFieldCol52 asf = new ActionSetFieldCol52();
        asf.setBoundName("x");
        asf.setFactField("age");
        asf.setType(DataType.TYPE_NUMERIC_INTEGER);
        asf.setUpdate(true);

        dt.getActionCols().add(asf);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{"1", "desc", "y", "old"}
        }));

        String drl = GuidedDTDRLPersistence.getInstance().marshal(dt);

        assertTrue(drl.indexOf("Context( )") > -1);
        assertTrue(drl.indexOf("modify( x ) {") > drl.indexOf("Context( )"));
        assertTrue(drl.indexOf("setAge(") > drl.indexOf("modify( x ) {"));

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{"1", "desc", null, "old"}
        }));

        drl = GuidedDTDRLPersistence.getInstance().marshal(dt);

        assertEquals(-1,
                     drl.indexOf("Context( )"));

        assertTrue(drl.indexOf("modify( x ) {") > -1);
        assertTrue(drl.indexOf("setAge(") > drl.indexOf("modify( x ) {"));

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{"1", "desc", null, null}
        }));

        drl = GuidedDTDRLPersistence.getInstance().marshal(dt);

        assertEquals(-1,
                     drl.indexOf("Context( )"));

        assertEquals(-1,
                     drl.indexOf("modify( x ) {"));
        assertEquals(-1,
                     drl.indexOf("setAge("));
    }

    @Test
    public void testUpdateModifyMultipleFields() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("x");
        p1.setFactType("Context");

        ConditionCol52 c = new ConditionCol52();
        c.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c);
        dt.getConditions().add(p1);

        ActionSetFieldCol52 asf1 = new ActionSetFieldCol52();
        asf1.setBoundName("x");
        asf1.setFactField("age");
        asf1.setType(DataType.TYPE_NUMERIC_INTEGER);
        asf1.setUpdate(true);

        dt.getActionCols().add(asf1);

        ActionSetFieldCol52 asf2 = new ActionSetFieldCol52();
        asf2.setBoundName("x");
        asf2.setFactField("name");
        asf2.setType(DataType.TYPE_STRING);
        asf2.setUpdate(true);

        dt.getActionCols().add(asf2);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{"1", "desc", "x", 55l, "Fred"}
        }));

        String drl = GuidedDTDRLPersistence.getInstance().marshal(dt);
        final String expected1 = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row 1 null\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  x : Context( )\n" +
                "then\n" +
                "  modify( x ) {\n" +
                "    setAge( 55 ), \n" +
                "    setName( \"Fred\" )\n" +
                "}\n" +
                "end\n";
        assertEqualsIgnoreWhitespace(expected1,
                                     drl);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{"1", "desc", "x", null, "Fred"}
        }));
        drl = GuidedDTDRLPersistence.getInstance().marshal(dt);
        final String expected2 = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row 1 null\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  x : Context( )\n" +
                "then\n" +
                "  modify( x ) {\n" +
                "    setName( \"Fred\" )\n" +
                "}\n" +
                "end\n";
        assertEqualsIgnoreWhitespace(expected2,
                                     drl);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{"1", "desc", "x", 55l, null}
        }));
        drl = GuidedDTDRLPersistence.getInstance().marshal(dt);
        final String expected3 = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row 1 null\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  x : Context( )\n" +
                "then\n" +
                "  modify( x ) {\n" +
                "    setAge( 55 ) \n" +
                "}\n" +
                "end\n";
        assertEqualsIgnoreWhitespace(expected3,
                                     drl);
    }

    @Test
    public void testUpdateModifyMultipleFieldsUpdateOneModifyTheOther() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("x");
        p1.setFactType("Context");

        ConditionCol52 c = new ConditionCol52();
        c.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c);
        dt.getConditions().add(p1);

        ActionSetFieldCol52 asf1 = new ActionSetFieldCol52();
        asf1.setBoundName("x");
        asf1.setFactField("age");
        asf1.setType(DataType.TYPE_NUMERIC_INTEGER);
        asf1.setUpdate(true);

        dt.getActionCols().add(asf1);

        ActionSetFieldCol52 asf2 = new ActionSetFieldCol52();
        asf2.setBoundName("x");
        asf2.setFactField("name");
        asf2.setType(DataType.TYPE_STRING);
        asf2.setUpdate(false);

        dt.getActionCols().add(asf2);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{"1", "desc", "x", 55l, "Fred"}
        }));

        String drl = GuidedDTDRLPersistence.getInstance().marshal(dt);
        final String expected1 = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row 1 null\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  x : Context( )\n" +
                "then\n" +
                "  modify( x ) {\n" +
                "    setAge( 55 ) \n" +
                "}\n" +
                "x.setName( \"Fred\" );\n" +
                "end\n";

        assertEqualsIgnoreWhitespace(expected1,
                                     drl);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{"1", "desc", "x", null, "Fred"}
        }));

        drl = GuidedDTDRLPersistence.getInstance().marshal(dt);
        final String expected2 = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row 1 null\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  x : Context( )\n" +
                "then\n" +
                "x.setName( \"Fred\" );\n" +
                "end\n";

        assertEqualsIgnoreWhitespace(expected2,
                                     drl);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{"1", "desc", "x", 55l, ""}
        }));

        drl = GuidedDTDRLPersistence.getInstance().marshal(dt);
        final String expected3 = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row 1 null\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  x : Context( )\n" +
                "then\n" +
                "  modify( x ) {\n" +
                "    setAge( 55 ) \n" +
                "}\n" +
                "end\n";

        assertEqualsIgnoreWhitespace(expected3,
                                     drl);
    }

    @Test
    public void testDefaultValue() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("$c");
        p1.setFactType("CheeseLover");

        ConditionCol52 c = new ConditionCol52();
        c.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c.setFactField("favouriteCheese");
        c.setDefaultValue(new DTCellValue52("cheddar"));
        c.setOperator("==");
        p1.getChildColumns().add(c);
        dt.getConditions().add(p1);

        //With provided getValue()
        String[][] data = new String[][]{
                new String[]{"1", "desc", "edam"},
        };
        dt.setData(DataUtilities.makeDataLists(data));

        String drl = GuidedDTDRLPersistence.getInstance().marshal(dt);

        assertFalse(drl.indexOf("$c : CheeseLover( favouriteCheese == \"edam\" )") == -1);

        //Without provided getValue() #1
        data = new String[][]{
                new String[]{"1", "desc", null},
        };
        dt.setData(DataUtilities.makeDataLists(data));

        drl = GuidedDTDRLPersistence.getInstance().marshal(dt);

        assertTrue(drl.indexOf("$c : CheeseLover( favouriteCheese == \"cheddar\" )") == -1);

        //Without provided getValue() #2
        data = new String[][]{
                new String[]{"1", "desc", ""},
        };
        dt.setData(DataUtilities.makeDataLists(data));

        drl = GuidedDTDRLPersistence.getInstance().marshal(dt);

        assertTrue(drl.indexOf("$c : CheeseLover( favouriteCheese == \"cheddar\" )") == -1);
    }

    @Test
    public void testLimitedEntryAttributes() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);
        dt.setTableName("limited-entry");

        AttributeCol52 attr = new AttributeCol52();
        attr.setAttribute("salience");
        dt.getAttributeCols().add(attr);

        dt.setData(DataUtilities.makeDataLists(new String[][]{
                new String[]{"1", "desc", "100"},
                new String[]{"2", "desc", "200"}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        assertTrue(drl.indexOf("salience 100") > -1);
        assertTrue(drl.indexOf("salience 200") > -1);
    }

    @Test
    public void testLimitedEntryMetadata() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);
        dt.setTableName("limited-entry");

        MetadataCol52 md = new MetadataCol52();
        md.setMetadata("metadata");
        dt.getMetadataCols().add(md);

        dt.setData(DataUtilities.makeDataLists(new String[][]{
                new String[]{"1", "desc", "md1"},
                new String[]{"2", "desc", "md2"}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        assertTrue(drl.indexOf("@metadata(md1)") > -1);
        assertTrue(drl.indexOf("@metadata(md2)") > -1);
    }

    @Test
    public void testLimitedEntryConditionsNoConstraints() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);
        dt.setTableName("limited-entry");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("p1");
        p1.setFactType("Smurf");
        dt.getConditions().add(p1);

        // This is a hack consistent with how the Expanded Form decision table
        // works. I wouldn't be too surprised if this changes at some time, but
        // GuidedDTDRLPersistence.marshal does not support empty patterns at
        // present.
        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc1.setValue(new DTCellValue52("y"));
        p1.getChildColumns().add(cc1);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc", true},
                new Object[]{2l, "desc", false}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        int index = -1;
        index = drl.indexOf("Smurf( )");
        assertTrue(index > -1);

        index = drl.indexOf("Smurf( )",
                            index + 1);
        assertFalse(index > -1);
    }

    @Test
    public void testLimitedEntryConditionsConstraints1() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);
        dt.setTableName("limited-entry");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("p1");
        p1.setFactType("Smurf");
        dt.getConditions().add(p1);

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc1.setFieldType(DataType.TYPE_STRING);
        cc1.setFactField("name");
        cc1.setOperator("==");
        cc1.setValue(new DTCellValue52("Pupa"));
        p1.getChildColumns().add(cc1);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc", true},
                new Object[]{2l, "desc", false}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        int index = -1;
        index = drl.indexOf("Smurf( name == \"Pupa\" )");
        assertTrue(index > -1);

        index = drl.indexOf("Smurf( name == \"Pupa\" )",
                            index + 1);
        assertFalse(index > -1);
    }

    @Test
    public void testLimitedEntryConditionsConstraints2() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);
        dt.setTableName("limited-entry");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("p1");
        p1.setFactType("Smurf");
        dt.getConditions().add(p1);

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc1.setFieldType(DataType.TYPE_STRING);
        cc1.setFactField("name");
        cc1.setOperator("==");
        cc1.setValue(new DTCellValue52("Pupa"));
        p1.getChildColumns().add(cc1);

        LimitedEntryConditionCol52 cc2 = new LimitedEntryConditionCol52();
        cc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc2.setFieldType(DataType.TYPE_STRING);
        cc2.setFactField("name");
        cc2.setOperator("==");
        cc2.setValue(new DTCellValue52("Smurfette"));
        p1.getChildColumns().add(cc2);

        LimitedEntryConditionCol52 cc3 = new LimitedEntryConditionCol52();
        cc3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc3.setFieldType(DataType.TYPE_STRING);
        cc3.setFactField("colour");
        cc3.setOperator("==");
        cc3.setValue(new DTCellValue52("Blue"));
        p1.getChildColumns().add(cc3);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc", true, false, true},
                new Object[]{2l, "desc", false, true, true},
                new Object[]{3l, "desc", false, false, true}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        int index = -1;
        index = drl.indexOf("Smurf( name == \"Pupa\" , colour == \"Blue\" )");
        assertTrue(index > -1);

        index = drl.indexOf("Smurf( name == \"Smurfette\" , colour == \"Blue\" )",
                            index + 1);
        assertTrue(index > -1);

        index = drl.indexOf("Smurf( colour == \"Blue\" )",
                            index + 1);
        assertTrue(index > -1);
    }

    @Test
    public void testLimitedEntryActionSet() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);
        dt.setTableName("limited-entry");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("p1");
        p1.setFactType("Smurf");
        dt.getConditions().add(p1);

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc1.setFieldType(DataType.TYPE_BOOLEAN);
        cc1.setFactField("isSmurf");
        cc1.setOperator("==");
        cc1.setValue(new DTCellValue52("true"));
        p1.getChildColumns().add(cc1);

        LimitedEntryActionSetFieldCol52 asf1 = new LimitedEntryActionSetFieldCol52();
        asf1.setBoundName("p1");
        asf1.setFactField("colour");
        asf1.setValue(new DTCellValue52("Blue"));

        dt.getActionCols().add(asf1);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc", true, true},
                new Object[]{2l, "desc", true, false}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        int index = -1;
        index = drl.indexOf("Smurf( isSmurf == true )");
        assertTrue(index > -1);
        index = drl.indexOf("p1.setColour( \"Blue\" )",
                            index + 1);
        assertTrue(index > -1);

        index = drl.indexOf("Smurf( isSmurf == true )",
                            index + 1);
        assertTrue(index > -1);
        index = drl.indexOf("p1.setColour( \"Blue\" )",
                            index + 1);
        assertFalse(index > -1);
    }

    @Test
    public void testLimitedEntryActionInsert() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);
        dt.setTableName("limited-entry");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("p1");
        p1.setFactType("Smurf");
        dt.getConditions().add(p1);

        LimitedEntryActionInsertFactCol52 asf1 = new LimitedEntryActionInsertFactCol52();
        asf1.setFactType("Smurf");
        asf1.setBoundName("s1");
        asf1.setFactField("colour");
        asf1.setValue(new DTCellValue52("Blue"));

        dt.getActionCols().add(asf1);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc", true},
                new Object[]{2l, "desc", false}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        int index = -1;
        index = drl.indexOf("Smurf s1 = new Smurf();");
        assertTrue(index > -1);
        index = drl.indexOf("s1.setColour( \"Blue\" );",
                            index + 1);
        assertTrue(index > -1);
        index = drl.indexOf("insert( s1 );",
                            index + 1);
        assertTrue(index > -1);

        int indexRule2 = index;
        indexRule2 = drl.indexOf("Smurf s1 = new Smurf();",
                                 index + 1);
        assertFalse(indexRule2 > -1);
        indexRule2 = drl.indexOf("s1.setColour( \"Blue\" );",
                                 index + 1);
        assertFalse(indexRule2 > -1);
        indexRule2 = drl.indexOf("insert(s1 );",
                                 index + 1);
        assertFalse(indexRule2 > -1);
    }

    @Test
    public void testLHSIsNullOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
        dt.setTableName("extended-entry");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("p1");
        p1.setFactType("Smurf");
        dt.getConditions().add(p1);

        ConditionCol52 cc1 = new ConditionCol52();
        cc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc1.setFieldType(DataType.TYPE_STRING);
        cc1.setFactField("name");
        cc1.setOperator("== null");
        p1.getChildColumns().add(cc1);

        ConditionCol52 cc2 = new ConditionCol52();
        cc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        cc2.setFactField("age");
        cc2.setOperator("== null");
        p1.getChildColumns().add(cc2);

        ConditionCol52 cc3 = new ConditionCol52();
        cc3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc3.setFieldType(DataType.TYPE_DATE);
        cc3.setFactField("dateOfBirth");
        cc3.setOperator("== null");
        p1.getChildColumns().add(cc3);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc", true, true, true},
                new Object[]{2l, "desc", false, false, false}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        int index = -1;
        index = drl.indexOf("Smurf( name == null , age == null , dateOfBirth == null )");
        assertTrue(index > -1);

        index = drl.indexOf("Smurf( )",
                            index + 1);
        assertFalse(index > -1);
    }

    @Test
    public void testLHSIsNullOperatorWithNullValues() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
        dt.setTableName("extended-entry");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("p1");
        p1.setFactType("Smurf");
        dt.getConditions().add(p1);

        ConditionCol52 cc1 = new ConditionCol52();
        cc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc1.setFieldType(DataType.TYPE_STRING);
        cc1.setFactField("name");
        cc1.setOperator("== null");
        p1.getChildColumns().add(cc1);

        ConditionCol52 cc2 = new ConditionCol52();
        cc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        cc2.setFactField("age");
        cc2.setOperator("== null");
        p1.getChildColumns().add(cc2);

        ConditionCol52 cc3 = new ConditionCol52();
        cc3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc3.setFieldType(DataType.TYPE_DATE);
        cc3.setFactField("dateOfBirth");
        cc3.setOperator("== null");
        p1.getChildColumns().add(cc3);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc", true, true, true},
                new Object[]{2l, "desc", null, null, null}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        int index = -1;
        index = drl.indexOf("Smurf( name == null , age == null , dateOfBirth == null )");
        assertTrue(index > -1);

        index = drl.indexOf("Smurf( )",
                            index + 1);
        assertFalse(index > -1);
    }

    @Test
    public void testLHSIsNotNullOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
        dt.setTableName("extended-entry");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("p1");
        p1.setFactType("Smurf");
        dt.getConditions().add(p1);

        ConditionCol52 cc1 = new ConditionCol52();
        cc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc1.setFieldType(DataType.TYPE_STRING);
        cc1.setFactField("name");
        cc1.setOperator("!= null");
        p1.getChildColumns().add(cc1);

        ConditionCol52 cc2 = new ConditionCol52();
        cc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        cc2.setFactField("age");
        cc2.setOperator("!= null");
        p1.getChildColumns().add(cc2);

        ConditionCol52 cc3 = new ConditionCol52();
        cc3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc3.setFieldType(DataType.TYPE_DATE);
        cc3.setFactField("dateOfBirth");
        cc3.setOperator("!= null");
        p1.getChildColumns().add(cc3);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc", true, true, true},
                new Object[]{2l, "desc", false, false, false}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        int index = -1;
        index = drl.indexOf("Smurf( name != null , age != null , dateOfBirth != null )");
        assertTrue(index > -1);

        index = drl.indexOf("Smurf( )",
                            index + 1);
        assertFalse(index > -1);
    }

    @Test
    public void testLHSIsNotNullOperatorWithNullValues() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
        dt.setTableName("extended-entry");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("p1");
        p1.setFactType("Smurf");
        dt.getConditions().add(p1);

        ConditionCol52 cc1 = new ConditionCol52();
        cc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc1.setFieldType(DataType.TYPE_STRING);
        cc1.setFactField("name");
        cc1.setOperator("!= null");
        p1.getChildColumns().add(cc1);

        ConditionCol52 cc2 = new ConditionCol52();
        cc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        cc2.setFactField("age");
        cc2.setOperator("!= null");
        p1.getChildColumns().add(cc2);

        ConditionCol52 cc3 = new ConditionCol52();
        cc3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc3.setFieldType(DataType.TYPE_DATE);
        cc3.setFactField("dateOfBirth");
        cc3.setOperator("!= null");
        p1.getChildColumns().add(cc3);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc", true, true, true},
                new Object[]{2l, "desc", null, null, null}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        int index = -1;
        index = drl.indexOf("Smurf( name != null , age != null , dateOfBirth != null )");
        assertTrue(index > -1);

        index = drl.indexOf("Smurf( )",
                            index + 1);
        assertFalse(index > -1);
    }

    @Test
    public void testLimitedEntryLHSIsNullOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);
        dt.setTableName("limited-entry");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("p1");
        p1.setFactType("Smurf");
        dt.getConditions().add(p1);

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc1.setFieldType(DataType.TYPE_STRING);
        cc1.setFactField("name");
        cc1.setOperator("== null");
        p1.getChildColumns().add(cc1);

        LimitedEntryConditionCol52 cc2 = new LimitedEntryConditionCol52();
        cc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        cc2.setFactField("age");
        cc2.setOperator("== null");
        p1.getChildColumns().add(cc2);

        LimitedEntryConditionCol52 cc3 = new LimitedEntryConditionCol52();
        cc3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc3.setFieldType(DataType.TYPE_DATE);
        cc3.setFactField("dateOfBirth");
        cc3.setOperator("== null");
        p1.getChildColumns().add(cc3);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc", true, true, true},
                new Object[]{2l, "desc", false, false, false}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        int index = -1;
        index = drl.indexOf("Smurf( name == null , age == null , dateOfBirth == null )");
        assertTrue(index > -1);

        index = drl.indexOf("Smurf( )",
                            index + 1);
        assertFalse(index > -1);
    }

    @Test
    public void testLimitedEntryLHSIsNullOperatorWithNullValues() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);
        dt.setTableName("limited-entry");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("p1");
        p1.setFactType("Smurf");
        dt.getConditions().add(p1);

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc1.setFieldType(DataType.TYPE_STRING);
        cc1.setFactField("name");
        cc1.setOperator("== null");
        p1.getChildColumns().add(cc1);

        LimitedEntryConditionCol52 cc2 = new LimitedEntryConditionCol52();
        cc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        cc2.setFactField("age");
        cc2.setOperator("== null");
        p1.getChildColumns().add(cc2);

        LimitedEntryConditionCol52 cc3 = new LimitedEntryConditionCol52();
        cc3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc3.setFieldType(DataType.TYPE_DATE);
        cc3.setFactField("dateOfBirth");
        cc3.setOperator("== null");
        p1.getChildColumns().add(cc3);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc", true, true, true},
                new Object[]{2l, "desc", null, null, null}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        int index = -1;
        index = drl.indexOf("Smurf( name == null , age == null , dateOfBirth == null )");
        assertTrue(index > -1);

        index = drl.indexOf("Smurf( )",
                            index + 1);
        assertFalse(index > -1);
    }

    @Test
    public void testLimitedEntryLHSIsNotNullOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);
        dt.setTableName("limited-entry");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("p1");
        p1.setFactType("Smurf");
        dt.getConditions().add(p1);

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc1.setFieldType(DataType.TYPE_STRING);
        cc1.setFactField("name");
        cc1.setOperator("!= null");
        p1.getChildColumns().add(cc1);

        LimitedEntryConditionCol52 cc2 = new LimitedEntryConditionCol52();
        cc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        cc2.setFactField("age");
        cc2.setOperator("!= null");
        p1.getChildColumns().add(cc2);

        LimitedEntryConditionCol52 cc3 = new LimitedEntryConditionCol52();
        cc3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc3.setFieldType(DataType.TYPE_DATE);
        cc3.setFactField("dateOfBirth");
        cc3.setOperator("!= null");
        p1.getChildColumns().add(cc3);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc", true, true, true},
                new Object[]{2l, "desc", false, false, false}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        int index = -1;
        index = drl.indexOf("Smurf( name != null , age != null , dateOfBirth != null )");
        assertTrue(index > -1);

        index = drl.indexOf("Smurf( )",
                            index + 1);
        assertFalse(index > -1);
    }

    @Test
    public void testLimitedEntryLHSIsNotNullOperatorWithNullValues() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);
        dt.setTableName("limited-entry");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("p1");
        p1.setFactType("Smurf");
        dt.getConditions().add(p1);

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc1.setFieldType(DataType.TYPE_STRING);
        cc1.setFactField("name");
        cc1.setOperator("!= null");
        p1.getChildColumns().add(cc1);

        LimitedEntryConditionCol52 cc2 = new LimitedEntryConditionCol52();
        cc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        cc2.setFactField("age");
        cc2.setOperator("!= null");
        p1.getChildColumns().add(cc2);

        LimitedEntryConditionCol52 cc3 = new LimitedEntryConditionCol52();
        cc3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc3.setFieldType(DataType.TYPE_DATE);
        cc3.setFactField("dateOfBirth");
        cc3.setOperator("!= null");
        p1.getChildColumns().add(cc3);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc", true, true, true},
                new Object[]{2l, "desc", null, null, null}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        int index = -1;
        index = drl.indexOf("Smurf( name != null , age != null , dateOfBirth != null )");
        assertTrue(index > -1);

        index = drl.indexOf("Smurf( )",
                            index + 1);
        assertFalse(index > -1);
    }

    @Test
    public void testLHSInOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
        dt.setTableName("extended-entry");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("p1");
        p1.setFactType("Smurf");
        dt.getConditions().add(p1);

        ConditionCol52 cc1 = new ConditionCol52();
        cc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc1.setFieldType(DataType.TYPE_STRING);
        cc1.setFactField("name");
        cc1.setOperator("in");
        p1.getChildColumns().add(cc1);

        ConditionCol52 cc2 = new ConditionCol52();
        cc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        cc2.setFactField("age");
        cc2.setOperator("in");
        p1.getChildColumns().add(cc2);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc", "Pupa, Brains, \"John, Snow\"", "55, 66"},
                new Object[]{2l, "desc", "", ""}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        int index = -1;
        index = drl.indexOf("Smurf( name in ( \"Pupa\", \"Brains\", \"John, Snow\" ) , age in ( 55, 66 ) )");
        assertTrue(index > -1);

        index = drl.indexOf("Smurf( )",
                            index + 1);
        assertFalse(index > -1);
    }

    @Test
    public void testLHSNotInOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
        dt.setTableName("extended-entry");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("p1");
        p1.setFactType("Smurf");
        dt.getConditions().add(p1);

        ConditionCol52 cc1 = new ConditionCol52();
        cc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc1.setFieldType(DataType.TYPE_STRING);
        cc1.setFactField("name");
        cc1.setOperator("not in");
        p1.getChildColumns().add(cc1);

        ConditionCol52 cc2 = new ConditionCol52();
        cc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        cc2.setFactField("age");
        cc2.setOperator("not in");
        p1.getChildColumns().add(cc2);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc", "Pupa, Brains", "55, 66"},
                new Object[]{2l, "desc", "", ""}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        int index = -1;
        index = drl.indexOf("Smurf( name not in ( \"Pupa\", \"Brains\" ) , age not in ( 55, 66 ) )");
        assertTrue(index > -1);

        index = drl.indexOf("Smurf( )",
                            index + 1);
        assertFalse(index > -1);
    }

    @Test
    public void testLimitedEntryLHSInOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);
        dt.setTableName("limited-entry");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("p1");
        p1.setFactType("Smurf");
        dt.getConditions().add(p1);

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc1.setFieldType(DataType.TYPE_STRING);
        cc1.setFactField("name");
        cc1.setOperator("in");
        cc1.setValue(new DTCellValue52("Pupa, Brains"));
        p1.getChildColumns().add(cc1);

        LimitedEntryConditionCol52 cc2 = new LimitedEntryConditionCol52();
        cc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        cc2.setFactField("age");
        cc2.setOperator("in");
        cc2.setValue(new DTCellValue52("55, 66"));
        p1.getChildColumns().add(cc2);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc", true, true},
                new Object[]{2l, "desc", false, false}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        int index = -1;
        index = drl.indexOf("Smurf( name in ( \"Pupa\", \"Brains\" ) , age in ( 55, 66 ) )");
        assertTrue(index > -1);

        index = drl.indexOf("Smurf( )",
                            index + 1);
        assertFalse(index > -1);
    }

    @Test
    public void testLimitedEntryLHSNotInOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);
        dt.setTableName("limited-entry");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("p1");
        p1.setFactType("Smurf");
        dt.getConditions().add(p1);

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc1.setFieldType(DataType.TYPE_STRING);
        cc1.setFactField("name");
        cc1.setOperator("not in");
        cc1.setValue(new DTCellValue52("Pupa, Brains"));
        p1.getChildColumns().add(cc1);

        LimitedEntryConditionCol52 cc2 = new LimitedEntryConditionCol52();
        cc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        cc2.setFactField("age");
        cc2.setOperator("not in");
        cc2.setValue(new DTCellValue52("55, 66"));
        p1.getChildColumns().add(cc2);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc", true, true},
                new Object[]{2l, "desc", false, false}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);

        int index = -1;
        index = drl.indexOf("Smurf( name not in ( \"Pupa\", \"Brains\" ) , age not in ( 55, 66 ) )");
        assertTrue(index > -1);

        index = drl.indexOf("Smurf( )",
                            index + 1);
        assertFalse(index > -1);
    }


    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testLHSWithBRLColumn_ParseToDRL() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        // All 3 rows should render, as the code is now lower down for skipping columns with empty cells
        String[][] data = new String[][]{
                new String[]{"1", "desc", "Gargamel", "Pupa", "50"},
                new String[]{"2", "desc", "Gargamel", "", "50"},
                new String[]{"3", "desc", "Gargamel", "Pupa", ""}
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol(new RowNumberCol52());
        dtable.setDescriptionCol(new DescriptionCol52());

        //Simple Condition
        Pattern52 p1 = new Pattern52();
        p1.setFactType("Baddie");

        ConditionCol52 con = new ConditionCol52();
        con.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        con.setFactField("name");
        con.setOperator("==");
        p1.getChildColumns().add(con);

        dtable.getConditions().add(p1);

        //BRL Column
        BRLConditionColumn brl1 = new BRLConditionColumn();

        //BRL Column definition
        List<IPattern> brl1Definition = new ArrayList<IPattern>();
        FactPattern brl1DefinitionFactPattern1 = new FactPattern("Smurf");

        SingleFieldConstraint brl1DefinitionFactPattern1Constraint1 = new SingleFieldConstraint();
        brl1DefinitionFactPattern1Constraint1.setFieldType(DataType.TYPE_STRING);
        brl1DefinitionFactPattern1Constraint1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        brl1DefinitionFactPattern1Constraint1.setFieldName("name");
        brl1DefinitionFactPattern1Constraint1.setOperator("==");
        brl1DefinitionFactPattern1Constraint1.setValue("$name");
        brl1DefinitionFactPattern1.addConstraint(brl1DefinitionFactPattern1Constraint1);

        SingleFieldConstraint brl1DefinitionFactPattern1Constraint2 = new SingleFieldConstraint();
        brl1DefinitionFactPattern1Constraint2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        brl1DefinitionFactPattern1Constraint2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        brl1DefinitionFactPattern1Constraint2.setFieldName("age");
        brl1DefinitionFactPattern1Constraint2.setOperator("==");
        brl1DefinitionFactPattern1Constraint2.setValue("$age");
        brl1DefinitionFactPattern1.addConstraint(brl1DefinitionFactPattern1Constraint2);

        brl1Definition.add(brl1DefinitionFactPattern1);

        brl1.setDefinition(brl1Definition);

        //Setup BRL column bindings
        BRLConditionVariableColumn brl1Variable1 = new BRLConditionVariableColumn("$name",
                                                                                  DataType.TYPE_STRING,
                                                                                  "Person",
                                                                                  "name");
        brl1.getChildColumns().add(brl1Variable1);
        BRLConditionVariableColumn brl1Variable2 = new BRLConditionVariableColumn("$age",
                                                                                  DataType.TYPE_NUMERIC_INTEGER,
                                                                                  "Person",
                                                                                  "age");
        brl1.getChildColumns().add(brl1Variable2);

        dtable.getConditions().add(brl1);
        dtable.setData(DataUtilities.makeDataLists(data));

        //Now to test conversion
        int ruleStartIndex;
        int pattern1StartIndex;
        int pattern2StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dtable);

        System.out.println(drl);

        //Row 0
        ruleStartIndex = drl.indexOf("//from row number: 1");
        assertFalse(ruleStartIndex == -1);
        pattern1StartIndex = drl.indexOf("Baddie( name == \"Gargamel\" )",
                                         ruleStartIndex);
        assertFalse(pattern1StartIndex == -1);
        pattern2StartIndex = drl.indexOf("Smurf( name == \"Pupa\" , age == 50 )",
                                         ruleStartIndex);
        assertFalse(pattern2StartIndex == -1);

        //Row 1
        ruleStartIndex = drl.indexOf("//from row number: 2");
        assertFalse(ruleStartIndex == -1);
        pattern1StartIndex = drl.indexOf("Baddie( name == \"Gargamel\" )",
                                         ruleStartIndex);
        assertFalse(pattern1StartIndex == -1);
        pattern2StartIndex = drl.indexOf("Smurf( age == 50 )",
                                         ruleStartIndex);
        assertFalse(pattern2StartIndex == -1);

        //Row 2
        ruleStartIndex = drl.indexOf("//from row number: 3");
        assertFalse(ruleStartIndex == -1);
        pattern1StartIndex = drl.indexOf("Baddie( name == \"Gargamel\" )",
                                         ruleStartIndex);
        assertFalse(pattern1StartIndex == -1);
        pattern2StartIndex = drl.indexOf("Smurf( name == \"Pupa\" )",
                                         ruleStartIndex);
        assertFalse(pattern2StartIndex == -1);
    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testLHSWithBRLColumn_ParseToDRL_MultiplePatterns() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        // All 3 rows should render, as the code is now lower down for skipping columns with empty cells
        String[][] data = new String[][]{
                new String[]{"1", "desc", "Pupa", "50"},
                new String[]{"2", "desc", "", "50"},
                new String[]{"3", "desc", "Pupa", ""}
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol(new RowNumberCol52());
        dtable.setDescriptionCol(new DescriptionCol52());

        //BRL Column
        BRLConditionColumn brl1 = new BRLConditionColumn();

        //BRL Column definition
        List<IPattern> brl1Definition = new ArrayList<IPattern>();
        FactPattern brl1DefinitionFactPattern1 = new FactPattern("Baddie");

        SingleFieldConstraint brl1DefinitionFactPattern1Constraint1 = new SingleFieldConstraint();
        brl1DefinitionFactPattern1Constraint1.setFieldType(DataType.TYPE_STRING);
        brl1DefinitionFactPattern1Constraint1.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        brl1DefinitionFactPattern1Constraint1.setFieldName("name");
        brl1DefinitionFactPattern1Constraint1.setOperator("==");
        brl1DefinitionFactPattern1Constraint1.setValue("Gargamel");
        brl1DefinitionFactPattern1.addConstraint(brl1DefinitionFactPattern1Constraint1);

        brl1Definition.add(brl1DefinitionFactPattern1);

        FactPattern brl1DefinitionFactPattern2 = new FactPattern("Smurf");

        SingleFieldConstraint brl1DefinitionFactPattern2Constraint1 = new SingleFieldConstraint();
        brl1DefinitionFactPattern2Constraint1.setFieldType(DataType.TYPE_STRING);
        brl1DefinitionFactPattern2Constraint1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        brl1DefinitionFactPattern2Constraint1.setFieldName("name");
        brl1DefinitionFactPattern2Constraint1.setOperator("==");
        brl1DefinitionFactPattern2Constraint1.setValue("$name");
        brl1DefinitionFactPattern2.addConstraint(brl1DefinitionFactPattern2Constraint1);

        SingleFieldConstraint brl1DefinitionFactPattern2Constraint2 = new SingleFieldConstraint();
        brl1DefinitionFactPattern2Constraint2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        brl1DefinitionFactPattern2Constraint2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        brl1DefinitionFactPattern2Constraint2.setFieldName("age");
        brl1DefinitionFactPattern2Constraint2.setOperator("==");
        brl1DefinitionFactPattern2Constraint2.setValue("$age");
        brl1DefinitionFactPattern2.addConstraint(brl1DefinitionFactPattern2Constraint2);

        brl1Definition.add(brl1DefinitionFactPattern2);

        brl1.setDefinition(brl1Definition);

        //Setup BRL column bindings
        BRLConditionVariableColumn brl1Variable1 = new BRLConditionVariableColumn("$name",
                                                                                  DataType.TYPE_STRING,
                                                                                  "Person",
                                                                                  "name");
        brl1.getChildColumns().add(brl1Variable1);
        BRLConditionVariableColumn brl1Variable2 = new BRLConditionVariableColumn("$age",
                                                                                  DataType.TYPE_NUMERIC_INTEGER,
                                                                                  "Person",
                                                                                  "age");
        brl1.getChildColumns().add(brl1Variable2);

        dtable.getConditions().add(brl1);
        dtable.setData(DataUtilities.makeDataLists(data));

        //Now to test conversion
        int ruleStartIndex;
        int pattern1StartIndex;
        int pattern2StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dtable);

        System.out.println(drl);

        //Row 0
        ruleStartIndex = drl.indexOf("//from row number: 1");
        assertFalse(ruleStartIndex == -1);
        pattern1StartIndex = drl.indexOf("Baddie( name == \"Gargamel\" )",
                                         ruleStartIndex);
        assertFalse(pattern1StartIndex == -1);
        pattern2StartIndex = drl.indexOf("Smurf( name == \"Pupa\" , age == 50 )",
                                         ruleStartIndex);
        assertFalse(pattern2StartIndex == -1);

        //Row 1
        ruleStartIndex = drl.indexOf("//from row number: 2");
        assertFalse(ruleStartIndex == -1);
        pattern1StartIndex = drl.indexOf("Baddie( name == \"Gargamel\" )",
                                         ruleStartIndex);
        assertFalse(pattern1StartIndex == -1);
        pattern2StartIndex = drl.indexOf("Smurf( age == 50 )",
                                         ruleStartIndex);
        assertFalse(pattern2StartIndex == -1);

        //Row 2
        ruleStartIndex = drl.indexOf("//from row number: 3");
        assertFalse(ruleStartIndex == -1);
        pattern1StartIndex = drl.indexOf("Baddie( name == \"Gargamel\" )",
                                         ruleStartIndex);
        assertFalse(pattern1StartIndex == -1);
        pattern2StartIndex = drl.indexOf("Smurf( name == \"Pupa\" )",
                                         ruleStartIndex);
        assertFalse(pattern2StartIndex == -1);
    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testLHSWithBRLColumn_ParseToDRL_NoVariables() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        //Row 0 should become an IPattern in the resulting RuleModel as it contains getValue()s for all Template fields in the BRL Column
        //Row 1 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain getValue()s for all Template fields in the BRL Column
        Object[][] data = new Object[][]{
                new Object[]{"1", "desc", Boolean.TRUE},
                new Object[]{"2", "desc", Boolean.FALSE}
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol(new RowNumberCol52());
        dtable.setDescriptionCol(new DescriptionCol52());

        //BRL Column
        BRLConditionColumn brl1 = new BRLConditionColumn();

        //BRL Column definition
        List<IPattern> brl1Definition = new ArrayList<IPattern>();
        FactPattern brl1DefinitionFactPattern1 = new FactPattern("Baddie");

        SingleFieldConstraint brl1DefinitionFactPattern1Constraint1 = new SingleFieldConstraint();
        brl1DefinitionFactPattern1Constraint1.setFieldType(DataType.TYPE_STRING);
        brl1DefinitionFactPattern1Constraint1.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        brl1DefinitionFactPattern1Constraint1.setFieldName("name");
        brl1DefinitionFactPattern1Constraint1.setOperator("==");
        brl1DefinitionFactPattern1Constraint1.setValue("Gargamel");
        brl1DefinitionFactPattern1.addConstraint(brl1DefinitionFactPattern1Constraint1);

        brl1Definition.add(brl1DefinitionFactPattern1);

        brl1.setDefinition(brl1Definition);

        //Setup BRL column bindings
        BRLConditionVariableColumn brl1Variable1 = new BRLConditionVariableColumn("",
                                                                                  DataType.TYPE_BOOLEAN);
        brl1.getChildColumns().add(brl1Variable1);

        dtable.getConditions().add(brl1);
        dtable.setData(DataUtilities.makeDataLists(data));

        //Now to test conversion
        int ruleStartIndex;
        int pattern1StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dtable);

        //Row 0
        ruleStartIndex = drl.indexOf("//from row number: 1");
        assertFalse(ruleStartIndex == -1);
        pattern1StartIndex = drl.indexOf("Baddie( name == \"Gargamel\" )",
                                         ruleStartIndex);
        assertFalse(pattern1StartIndex == -1);

        //Row 1
        ruleStartIndex = drl.indexOf("//from row number: 2");
        assertFalse(ruleStartIndex == -1);
        pattern1StartIndex = drl.indexOf("Baddie( name == \"Gargamel\" )",
                                         ruleStartIndex);
        assertTrue(pattern1StartIndex == -1);
    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testLHSWithBRLColumn_ParseToDRL_FreeFormLine() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        //Row 0 should become an IPattern in the resulting RuleModel as it contains values for all Template fields in the BRL Column
        //Row 1 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        //Row 2 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        //Row 3 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        String[][] data = new String[][]{
                new String[]{"1", "desc", "Pupa", "50"},
                new String[]{"2", "desc", "", "50"},
                new String[]{"3", "desc", "Pupa", ""},
                new String[]{"4", "desc", "", ""}
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol(new RowNumberCol52());
        dtable.setDescriptionCol(new DescriptionCol52());

        //BRL Column
        BRLConditionColumn brl1 = new BRLConditionColumn();

        //BRL Column definition
        List<IPattern> brl1Definition = new ArrayList<IPattern>();
        FreeFormLine brl1DefinitionFreeFormLine = new FreeFormLine();
        brl1DefinitionFreeFormLine.setText("Smurf( name == \"@{name}\", age == @{age} )");

        brl1Definition.add(brl1DefinitionFreeFormLine);

        brl1.setDefinition(brl1Definition);

        //Setup BRL column bindings
        BRLConditionVariableColumn brl1Variable1 = new BRLConditionVariableColumn("name",
                                                                                  DataType.TYPE_STRING);
        BRLConditionVariableColumn brl1Variable2 = new BRLConditionVariableColumn("age",
                                                                                  DataType.TYPE_NUMERIC_INTEGER);
        brl1.getChildColumns().add(brl1Variable1);
        brl1.getChildColumns().add(brl1Variable2);

        dtable.getConditions().add(brl1);
        dtable.setData(DataUtilities.makeDataLists(data));

        //Now to test conversion
        int ruleStartIndex;
        int pattern1StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dtable);

        //Row 0
        ruleStartIndex = drl.indexOf("//from row number: 1");
        assertFalse(ruleStartIndex == -1);
        pattern1StartIndex = drl.indexOf("Smurf( name == \"Pupa\", age == 50 )",
                                         ruleStartIndex);
        assertFalse(pattern1StartIndex == -1);

        //Row 1
        ruleStartIndex = drl.indexOf("//from row number: 2");
        assertFalse(ruleStartIndex == -1);
        pattern1StartIndex = drl.indexOf("Smurf(",
                                         ruleStartIndex);
        assertTrue(pattern1StartIndex == -1);

        //Row 2
        ruleStartIndex = drl.indexOf("//from row number: 3");
        assertFalse(ruleStartIndex == -1);
        pattern1StartIndex = drl.indexOf("Smurf(",
                                         ruleStartIndex);
        assertTrue(pattern1StartIndex == -1);

        //Row 3
        ruleStartIndex = drl.indexOf("//from row number: 4");
        assertFalse(ruleStartIndex == -1);
        pattern1StartIndex = drl.indexOf("Smurf(",
                                         ruleStartIndex);
        assertTrue(pattern1StartIndex == -1);
    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testRHSWithBRLColumn_ParseToDRL() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        // All three rows are entered, some columns with optional data
        String[][] data = new String[][]{
                new String[]{"1", "desc", "Gargamel", "Pupa", "50"},
                new String[]{"2", "desc", "Gargamel", "", "50"},
                new String[]{"3", "desc", "Gargamel", "Pupa", ""}
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol(new RowNumberCol52());
        dtable.setDescriptionCol(new DescriptionCol52());

        //Simple Action
        ActionInsertFactCol52 a1 = new ActionInsertFactCol52();
        a1.setBoundName("$b");
        a1.setFactType("Baddie");
        a1.setFactField("name");
        a1.setType(DataType.TYPE_STRING);

        dtable.getActionCols().add(a1);

        //BRL Column
        BRLActionColumn brl1 = new BRLActionColumn();

        //BRL Column definition
        List<IAction> brl1Definition = new ArrayList<IAction>();
        ActionInsertFact brl1DefinitionAction1 = new ActionInsertFact("Smurf");
        ActionFieldValue brl1DefinitionAction1FieldValue1 = new ActionFieldValue("name",
                                                                                 "$name",
                                                                                 DataType.TYPE_STRING);
        brl1DefinitionAction1FieldValue1.setNature(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        brl1DefinitionAction1.addFieldValue(brl1DefinitionAction1FieldValue1);
        ActionFieldValue brl1DefinitionAction1FieldValue2 = new ActionFieldValue("age",
                                                                                 "$age",
                                                                                 DataType.TYPE_NUMERIC_INTEGER);
        brl1DefinitionAction1FieldValue2.setNature(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        brl1DefinitionAction1.addFieldValue(brl1DefinitionAction1FieldValue2);
        brl1Definition.add(brl1DefinitionAction1);
        brl1.setDefinition(brl1Definition);

        //Setup BRL column bindings
        BRLActionVariableColumn brl1Variable1 = new BRLActionVariableColumn("$name",
                                                                            DataType.TYPE_STRING,
                                                                            "Person",
                                                                            "name");
        brl1.getChildColumns().add(brl1Variable1);
        BRLActionVariableColumn brl1Variable2 = new BRLActionVariableColumn("$age",
                                                                            DataType.TYPE_NUMERIC_INTEGER,
                                                                            "Person",
                                                                            "age");
        brl1.getChildColumns().add(brl1Variable2);

        dtable.getActionCols().add(brl1);
        dtable.setData(DataUtilities.makeDataLists(data));

        //Now to test conversion
        int ruleStartIndex;
        int action1StartIndex;
        int action2StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dtable);

        //Row 0
        ruleStartIndex = drl.indexOf("//from row number: 1");
        assertFalse(ruleStartIndex == -1);
        action1StartIndex = drl.indexOf("Baddie $b = new Baddie();",
                                        ruleStartIndex);
        assertFalse(action1StartIndex == -1);
        action1StartIndex = drl.indexOf("$b.setName( \"Gargamel\" );",
                                        action1StartIndex);
        assertFalse(action1StartIndex == -1);
        action1StartIndex = drl.indexOf("insert( $b );",
                                        action1StartIndex);
        assertFalse(action1StartIndex == -1);

        action2StartIndex = drl.indexOf("Smurf fact0 = new Smurf();",
                                        ruleStartIndex);
        assertFalse(action2StartIndex == -1);
        action2StartIndex = drl.indexOf("fact0.setName( \"Pupa\" );",
                                        action2StartIndex);
        assertFalse(action2StartIndex == -1);
        action2StartIndex = drl.indexOf("fact0.setAge( 50 );",
                                        action2StartIndex);
        assertFalse(action2StartIndex == -1);
        action2StartIndex = drl.indexOf("insert( fact0 );",
                                        action2StartIndex);
        assertFalse(action2StartIndex == -1);

        //Row 1
        ruleStartIndex = drl.indexOf("//from row number: 2");
        int ruleEndIndex = drl.indexOf("//from row number: 3");
        assertFalse(ruleStartIndex == -1);
        action1StartIndex = drl.indexOf("Baddie $b = new Baddie();",
                                        ruleStartIndex);
        assertFalse(action1StartIndex == -1);
        action1StartIndex = drl.indexOf("$b.setName( \"Gargamel\" );",
                                        action1StartIndex);
        assertFalse(action1StartIndex == -1);
        action1StartIndex = drl.indexOf("insert( $b );",
                                        action1StartIndex);
        assertFalse(action1StartIndex == -1);

        action2StartIndex = drl.indexOf("Smurf fact0 = new Smurf();",
                                        ruleStartIndex);
        assertFalse(action2StartIndex == -1);
        action2StartIndex = drl.indexOf("fact0.setName(",
                                        ruleStartIndex);
        assertFalse(action2StartIndex < ruleEndIndex);
        action2StartIndex = drl.indexOf("fact0.setAge( 50 );",
                                        ruleStartIndex);
        assertFalse(action2StartIndex == -1);
        action2StartIndex = drl.indexOf("insert( fact0 );",
                                        ruleStartIndex);
        assertFalse(action2StartIndex == -1);

        //Row 2
        ruleStartIndex = drl.indexOf("//from row number: 3");
        assertFalse(ruleStartIndex == -1);
        action1StartIndex = drl.indexOf("Baddie $b = new Baddie();",
                                        ruleStartIndex);
        assertFalse(action1StartIndex == -1);
        action1StartIndex = drl.indexOf("$b.setName( \"Gargamel\" );",
                                        action1StartIndex);
        assertFalse(action1StartIndex == -1);
        action1StartIndex = drl.indexOf("insert( $b );",
                                        action1StartIndex);
        assertFalse(action1StartIndex == -1);

        action2StartIndex = drl.indexOf("Smurf fact0 = new Smurf();",
                                        ruleStartIndex);
        assertFalse(action2StartIndex == -1);
        action2StartIndex = drl.indexOf("fact0.setName( \"Pupa\" );",
                                        ruleStartIndex);
        assertFalse(action2StartIndex == -1);
        action2StartIndex = drl.indexOf("fact0.setAge( 50 );",
                                        ruleStartIndex);
        assertTrue(action2StartIndex == -1);
        action2StartIndex = drl.indexOf("insert( fact0 );",
                                        ruleStartIndex);
        assertFalse(action2StartIndex == -1);
    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testRHSWithBRLColumn_ParseToDRL_MultipleActions() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        // All three rows are entered, some columns with optional data
        String[][] data = new String[][]{
                new String[]{"1", "desc", "Pupa", "50"},
                new String[]{"2", "desc", "", "50"},
                new String[]{"3", "desc", "Pupa", ""}
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol(new RowNumberCol52());
        dtable.setDescriptionCol(new DescriptionCol52());

        //BRL Column
        BRLActionColumn brl1 = new BRLActionColumn();

        //BRL Column definition
        List<IAction> brl1Definition = new ArrayList<IAction>();
        ActionInsertFact brl1DefinitionAction1 = new ActionInsertFact("Baddie");
        ActionFieldValue brl1DefinitionAction1FieldValue1 = new ActionFieldValue("name",
                                                                                 "Gargamel",
                                                                                 DataType.TYPE_STRING);
        brl1DefinitionAction1FieldValue1.setNature(BaseSingleFieldConstraint.TYPE_LITERAL);
        brl1DefinitionAction1.addFieldValue(brl1DefinitionAction1FieldValue1);
        brl1Definition.add(brl1DefinitionAction1);

        ActionInsertFact brl1DefinitionAction2 = new ActionInsertFact("Smurf");
        ActionFieldValue brl1DefinitionAction2FieldValue1 = new ActionFieldValue("name",
                                                                                 "$name",
                                                                                 DataType.TYPE_STRING);
        brl1DefinitionAction2FieldValue1.setNature(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        brl1DefinitionAction2.addFieldValue(brl1DefinitionAction2FieldValue1);
        ActionFieldValue brl1DefinitionAction2FieldValue2 = new ActionFieldValue("age",
                                                                                 "$age",
                                                                                 DataType.TYPE_NUMERIC_INTEGER);
        brl1DefinitionAction2FieldValue2.setNature(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        brl1DefinitionAction2.addFieldValue(brl1DefinitionAction2FieldValue2);
        brl1Definition.add(brl1DefinitionAction2);

        brl1.setDefinition(brl1Definition);

        //Setup BRL column bindings
        BRLActionVariableColumn brl1Variable1 = new BRLActionVariableColumn("$name",
                                                                            DataType.TYPE_STRING,
                                                                            "Person",
                                                                            "name");
        brl1.getChildColumns().add(brl1Variable1);
        BRLActionVariableColumn brl1Variable2 = new BRLActionVariableColumn("$age",
                                                                            DataType.TYPE_NUMERIC_INTEGER,
                                                                            "Person",
                                                                            "age");
        brl1.getChildColumns().add(brl1Variable2);

        dtable.getActionCols().add(brl1);
        dtable.setData(DataUtilities.makeDataLists(data));

        //Now to test conversion
        int ruleStartIndex;
        int action1StartIndex;
        int action2StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dtable);

        //Row 0
        ruleStartIndex = drl.indexOf("//from row number: 1");
        assertFalse(ruleStartIndex == -1);
        action1StartIndex = drl.indexOf("Baddie fact0 = new Baddie();",
                                        ruleStartIndex);
        assertFalse(action1StartIndex == -1);
        action1StartIndex = drl.indexOf("fact0.setName( \"Gargamel\" );",
                                        action1StartIndex);
        assertFalse(action1StartIndex == -1);
        action1StartIndex = drl.indexOf("insert( fact0 );",
                                        action1StartIndex);
        assertFalse(action1StartIndex == -1);

        action2StartIndex = drl.indexOf("Smurf fact1 = new Smurf();",
                                        ruleStartIndex);
        assertFalse(action2StartIndex == -1);
        action2StartIndex = drl.indexOf("fact1.setName( \"Pupa\" );",
                                        action2StartIndex);
        assertFalse(action2StartIndex == -1);
        action2StartIndex = drl.indexOf("fact1.setAge( 50 );",
                                        action2StartIndex);
        assertFalse(action2StartIndex == -1);
        action2StartIndex = drl.indexOf("insert( fact1 );",
                                        action2StartIndex);
        assertFalse(action2StartIndex == -1);

        //Row 1
        ruleStartIndex = drl.indexOf("//from row number: 2");
        int ruleEndIndex = drl.indexOf("//from row number: 3");
        assertFalse(ruleStartIndex == -1);
        action1StartIndex = drl.indexOf("Baddie fact0 = new Baddie();",
                                        ruleStartIndex);
        assertFalse(action1StartIndex == -1);
        action1StartIndex = drl.indexOf("fact0.setName( \"Gargamel\" );",
                                        action1StartIndex);
        assertFalse(action1StartIndex == -1);
        action1StartIndex = drl.indexOf("insert( fact0 );",
                                        action1StartIndex);
        assertFalse(action1StartIndex == -1);

        action2StartIndex = drl.indexOf("Smurf fact1 = new Smurf();",
                                        ruleStartIndex);
        assertFalse(action2StartIndex == -1);
        action2StartIndex = drl.indexOf("fact1.setName( \"Pupa\" );",
                                        ruleStartIndex);
        assertFalse(action2StartIndex < ruleEndIndex);
        action2StartIndex = drl.indexOf("fact1.setAge( 50 );",
                                        ruleStartIndex);
        assertFalse(action2StartIndex == -1);
        action2StartIndex = drl.indexOf("insert( fact1 );",
                                        ruleStartIndex);
        assertFalse(action2StartIndex == -1);

        //Row 2
        ruleStartIndex = drl.indexOf("//from row number: 3");
        assertFalse(ruleStartIndex == -1);
        action1StartIndex = drl.indexOf("Baddie fact0 = new Baddie();",
                                        ruleStartIndex);
        assertFalse(action1StartIndex == -1);
        action1StartIndex = drl.indexOf("fact0.setName( \"Gargamel\" );",
                                        action1StartIndex);
        assertFalse(action1StartIndex == -1);
        action1StartIndex = drl.indexOf("insert( fact0 );",
                                        action1StartIndex);
        assertFalse(action1StartIndex == -1);

        action2StartIndex = drl.indexOf("Smurf fact1 = new Smurf();",
                                        ruleStartIndex);
        assertFalse(action2StartIndex == -1);
        action2StartIndex = drl.indexOf("fact1.setName( \"Pupa\" );",
                                        ruleStartIndex);
        assertFalse(action2StartIndex == -1);
        action2StartIndex = drl.indexOf("fact1.setAge( 50 );",
                                        ruleStartIndex);
        assertTrue(action2StartIndex == -1);
        action2StartIndex = drl.indexOf("insert( fact1 );",
                                        ruleStartIndex);
        assertFalse(action2StartIndex == -1);
    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testRHSWithBRLColumn_ParseToDRL_NoVariables() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        Object[][] data = new Object[][]{
                new Object[]{"1", "desc", Boolean.TRUE},
                new Object[]{"2", "desc", Boolean.FALSE}
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol(new RowNumberCol52());
        dtable.setDescriptionCol(new DescriptionCol52());

        //BRL Column
        BRLActionColumn brl1 = new BRLActionColumn();

        //BRL Column definition
        List<IAction> brl1Definition = new ArrayList<IAction>();
        ActionInsertFact brl1DefinitionAction1 = new ActionInsertFact("Baddie");
        ActionFieldValue brl1DefinitionAction1FieldValue1 = new ActionFieldValue("name",
                                                                                 "Gargamel",
                                                                                 DataType.TYPE_STRING);
        brl1DefinitionAction1FieldValue1.setNature(BaseSingleFieldConstraint.TYPE_LITERAL);
        brl1DefinitionAction1.addFieldValue(brl1DefinitionAction1FieldValue1);
        brl1Definition.add(brl1DefinitionAction1);

        brl1.setDefinition(brl1Definition);

        //Setup BRL column bindings
        BRLActionVariableColumn brl1Variable1 = new BRLActionVariableColumn("",
                                                                            DataType.TYPE_BOOLEAN);
        brl1.getChildColumns().add(brl1Variable1);

        dtable.getActionCols().add(brl1);
        dtable.setData(DataUtilities.makeDataLists(data));

        //Now to test conversion
        int ruleStartIndex;
        int action1StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dtable);

        //Row 0
        ruleStartIndex = drl.indexOf("//from row number: 1");
        assertFalse(ruleStartIndex == -1);
        action1StartIndex = drl.indexOf("Baddie fact0 = new Baddie();",
                                        ruleStartIndex);
        assertFalse(action1StartIndex == -1);
        action1StartIndex = drl.indexOf("fact0.setName( \"Gargamel\" );",
                                        action1StartIndex);
        assertFalse(action1StartIndex == -1);
        action1StartIndex = drl.indexOf("insert( fact0 );",
                                        action1StartIndex);
        assertFalse(action1StartIndex == -1);

        //Row 1
        ruleStartIndex = drl.indexOf("//from row number: 2");
        assertFalse(ruleStartIndex == -1);
        action1StartIndex = drl.indexOf("Baddie fact0 = new Baddie();",
                                        ruleStartIndex);
        assertTrue(action1StartIndex == -1);
        action1StartIndex = drl.indexOf("fact0.setName( \"Gargamel\" );",
                                        ruleStartIndex);
        assertTrue(action1StartIndex == -1);
        action1StartIndex = drl.indexOf("insert( fact0 );",
                                        ruleStartIndex);
        assertTrue(action1StartIndex == -1);
    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testRHSWithBRLColumn_ParseToDRL_FreeFormLine() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        //Row 0 should become an IAction in the resulting RuleModel as it contains values for all Template fields in the BRL Column
        //Row 1 should *NOT* become an IAction in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        //Row 2 should *NOT* become an IAction in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        //Row 3 should *NOT* become an IAction in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        String[][] data = new String[][]{
                new String[]{"1", "desc", "Pupa", "50"},
                new String[]{"2", "desc", "", "50"},
                new String[]{"3", "desc", "Pupa", ""},
                new String[]{"4", "desc", "", ""}
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol(new RowNumberCol52());
        dtable.setDescriptionCol(new DescriptionCol52());

        //BRL Action
        BRLActionColumn brl1 = new BRLActionColumn();

        //BRL Action definition
        List<IAction> brl1Definition = new ArrayList<IAction>();
        FreeFormLine brl1DefinitionFreeFormLine = new FreeFormLine();
        brl1DefinitionFreeFormLine.setText("System.out.println( \"name == @{name}, age == @{age}\" );");

        brl1Definition.add(brl1DefinitionFreeFormLine);

        brl1.setDefinition(brl1Definition);

        //Setup BRL column bindings
        BRLActionVariableColumn brl1Variable1 = new BRLActionVariableColumn("name",
                                                                            DataType.TYPE_STRING);
        BRLActionVariableColumn brl1Variable2 = new BRLActionVariableColumn("age",
                                                                            DataType.TYPE_NUMERIC_INTEGER);
        brl1.getChildColumns().add(brl1Variable1);
        brl1.getChildColumns().add(brl1Variable2);

        dtable.getActionCols().add(brl1);
        dtable.setData(DataUtilities.makeDataLists(data));

        //Now to test conversion
        int ruleStartIndex;
        int pattern1StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dtable);

        //Row 0
        ruleStartIndex = drl.indexOf("//from row number: 1");
        assertFalse(ruleStartIndex == -1);
        pattern1StartIndex = drl.indexOf("System.out.println( \"name == Pupa, age == 50\" );",
                                         ruleStartIndex);
        assertFalse(pattern1StartIndex == -1);

        //Row 1
        ruleStartIndex = drl.indexOf("//from row number: 2");
        assertFalse(ruleStartIndex == -1);
        pattern1StartIndex = drl.indexOf("System.out.println(",
                                         ruleStartIndex);
        assertTrue(pattern1StartIndex == -1);

        //Row 2
        ruleStartIndex = drl.indexOf("//from row number: 3");
        assertFalse(ruleStartIndex == -1);
        pattern1StartIndex = drl.indexOf("System.out.println(",
                                         ruleStartIndex);
        assertTrue(pattern1StartIndex == -1);

        //Row 3
        ruleStartIndex = drl.indexOf("//from row number: 4");
        assertFalse(ruleStartIndex == -1);
        pattern1StartIndex = drl.indexOf("System.out.println(",
                                         ruleStartIndex);
        assertTrue(pattern1StartIndex == -1);
    }
    
    
    

    @Test
    public void testPackageNameAndImports() throws Exception {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setPackageName("org.drools.guvnor.models.guided.dtable.backend");
        dt.getImports().addImport(new Import("java.lang.String"));

        dt.setTableName("michael");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("f1");
        p1.setFactType("Driver");

        ConditionCol52 con = new ConditionCol52();
        con.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        con.setFactField("age");
        con.setHeader("Driver f1 age");
        con.setOperator("==");
        p1.getChildColumns().add(con);

        dt.getConditions().add(p1);

        dt.setData(DataUtilities.makeDataLists(new String[][]{
                new String[]{"1", "desc", "42"}
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);
        System.out.println(drl);

        assertTrue(drl.indexOf("package org.drools.guvnor.models.guided.dtable.backend;") == 0);
        assertTrue(drl.indexOf("import java.lang.String;") > 0);
    }

    @Test
    public void testLHSNonEmptyStringValues() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
        dt.setTableName("extended-entry");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("p1");
        p1.setFactType("Smurf");
        dt.getConditions().add(p1);

        ConditionCol52 cc1 = new ConditionCol52();
        cc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc1.setFieldType(DataType.TYPE_STRING);
        cc1.setFactField("name");
        cc1.setOperator("==");
        p1.getChildColumns().add(cc1);

        ConditionCol52 cc2 = new ConditionCol52();
        cc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        cc2.setFactField("age");
        cc2.setOperator("==");
        p1.getChildColumns().add(cc2);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc-row1", null, null},
                new Object[]{2l, "desc-row2", "   ", 35l},
                new Object[]{3l, "desc-row3", "", null},
                new Object[]{4l, "desc-row4", "", 35l},
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);
        System.out.println(drl);
        
        final String expected = "//from row number: 1\n" +
                "//desc-row1\n" +
                "rule \"Row 1 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end\n" +
                "//from row number: 2\n" +
                "//desc-row2\n" +
                "rule \"Row 2 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( age == 35 )\n" +
                "  then\n" +
                "end\n" +
                "//from row number: 3\n" +
                "//desc-row3\n" +
                "rule \"Row 3 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end\n" +
                "//from row number: 4\n" +
                "//desc-row4\n" +
                "rule \"Row 4 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( age == 35 )\n" +
                "  then\n" +
                "end";

        assertEqualsIgnoreWhitespace(expected,
                                     drl);
    }

    @Test
    public void testLHSDelimitedNonEmptyStringValues() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
        dt.setTableName("extended-entry");

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("p1");
        p1.setFactType("Smurf");
        dt.getConditions().add(p1);

        ConditionCol52 cc1 = new ConditionCol52();
        cc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc1.setFieldType(DataType.TYPE_STRING);
        cc1.setFactField("name");
        cc1.setOperator("==");
        p1.getChildColumns().add(cc1);

        ConditionCol52 cc2 = new ConditionCol52();
        cc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        cc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        cc2.setFactField("age");
        cc2.setOperator("==");
        p1.getChildColumns().add(cc2);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc-row1", null, null},
                new Object[]{2l, "desc-row2", "\"   \"", 35l},
                new Object[]{3l, "desc-row3", "\"\"", null},
                new Object[]{4l, "desc-row4", "\"\"", 35l},
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);
        System.out.println(drl);
        
        final String expected = "//from row number: 1\n" +
                "//desc-row1\n" +
                "rule \"Row 1 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end\n" +
                "//from row number: 2\n" +
                "//desc-row2\n" +
                "rule \"Row 2 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( name == \"   \", age == 35 )\n" +
                "  then\n" +
                "end\n" +
                "//from row number: 3\n" +
                "//desc-row3\n" +
                "rule \"Row 3 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( name == \"\" )\n" +
                "  then\n" +
                "end\n" +
                "//from row number: 4\n" +
                "//desc-row4\n" +
                "rule \"Row 4 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( name == \"\", age == 35 )\n" +
                "  then\n" +
                "end";

        assertEqualsIgnoreWhitespace(expected,
                                     drl);
    }

    @Test
    public void testRHSNonEmptyStringValues() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
        dt.setTableName("extended-entry");

        ActionInsertFactCol52 ins1 = new ActionInsertFactCol52();
        ins1.setBoundName("$f");
        ins1.setFactType("Smurf");
        ins1.setFactField("name");
        ins1.setType(DataType.TYPE_STRING);
        dt.getActionCols().add(ins1);

        ActionInsertFactCol52 ins2 = new ActionInsertFactCol52();
        ins2.setBoundName("$f");
        ins2.setFactType("Smurf");
        ins2.setFactField("age");
        ins2.setType(DataType.TYPE_NUMERIC_INTEGER);
        dt.getActionCols().add(ins2);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc-row1", null, null},
                new Object[]{2l, "desc-row2", "   ", 35l},
                new Object[]{3l, "desc-row3", "", null},
                new Object[]{4l, "desc-row4", "", 35l},
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);
        System.out.println(drl);
        
        
        final String expected = "//from row number: 1\n" +
                "//desc-row1\n" +
                "rule \"Row 1 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end\n" +
                "//from row number: 2\n" +
                "//desc-row2\n" +
                "rule \"Row 2 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "    Smurf $f = new Smurf();\n" +
                "    $f.setAge( 35 );\n" +
                "    insert( $f );\n" +
                "end\n" +
                "//from row number: 3\n" +
                "//desc-row3\n" +
                "rule \"Row 3 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end\n" +
                "//from row number: 4\n" +
                "//desc-row4\n" +
                "rule \"Row 4 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "    Smurf $f = new Smurf();\n" +
                "    $f.setAge( 35 );\n" +
                "    insert( $f );\n" +
                "end";

        assertEqualsIgnoreWhitespace(expected,
                                     drl);
    }

    @Test
    public void testRHSDelimitedNonEmptyStringValues() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
        dt.setTableName("extended-entry");

        ActionInsertFactCol52 ins1 = new ActionInsertFactCol52();
        ins1.setBoundName("$f");
        ins1.setFactType("Smurf");
        ins1.setFactField("name");
        ins1.setType(DataType.TYPE_STRING);
        dt.getActionCols().add(ins1);

        ActionInsertFactCol52 ins2 = new ActionInsertFactCol52();
        ins2.setBoundName("$f");
        ins2.setFactType("Smurf");
        ins2.setFactField("age");
        ins2.setType(DataType.TYPE_NUMERIC_INTEGER);
        dt.getActionCols().add(ins2);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{1l, "desc-row1", null, null},
                new Object[]{2l, "desc-row2", "\"   \"", 35l},
                new Object[]{3l, "desc-row3", "\"\"", null},
                new Object[]{4l, "desc-row4", "\"\"", 35l},
        }));

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);
        System.out.println(drl);
        
        
        final String expected = "//from row number: 1\n" +
                "//desc-row1\n" +
                "rule \"Row 1 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end\n" +
                "//from row number: 2\n" +
                "//desc-row2\n" +
                "rule \"Row 2 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "    Smurf $f = new Smurf();\n" +
                "    $f.setName( \"   \" );\n" +
                "    $f.setAge( 35 );\n" +
                "    insert( $f );\n" +
                "end\n" +
                "//from row number: 3\n" +
                "//desc-row3\n" +
                "rule \"Row 3 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "    Smurf $f = new Smurf();\n" +
                "    $f.setName( \"\" );\n" +
                "    insert( $f );\n" +
                "end\n" +
                "//from row number: 4\n" +
                "//desc-row4\n" +
                "rule \"Row 4 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "    Smurf $f = new Smurf();\n" +
                "    $f.setName( \"\" );\n" +
                "    $f.setAge( 35 );\n" +
                "    insert( $f );\n" +
                "end";

        assertEqualsIgnoreWhitespace(expected,
                                     drl);
    }

    private void assertEqualsIgnoreWhitespace(final String expected,
                                              final String actual) {
        //Assertions.assertThat(expected).isEqualToIgnoringWhitespace(actual);
    }
}
