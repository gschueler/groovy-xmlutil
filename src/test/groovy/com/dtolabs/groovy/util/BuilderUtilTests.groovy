package com.dtolabs.groovy.util

import groovy.xml.MarkupBuilder

class BuilderUtilTests extends GroovyTestCase {
    static String NL = System.getProperty('line.separator')

    void assertMapToDom(String value, Map map) {
        def writer = new StringWriter()
        def mp = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), '', false))
        def bu = new BuilderUtil()
        bu.mapToDom(map, mp)
        assert value == writer.toString()
    }

    void testBasic() {
        assertMapToDom '<blah>blee</blah>', [blah: 'blee']
    }

    void testMap() {
        assertMapToDom '<blah><bloo>blee</bloo></blah>', [blah: [bloo: 'blee']]
    }

    void testCollection() {
        assertMapToDom '<blah>blee</blah>', [blah: ['blee']]
        assertMapToDom '<blah>blee</blah><blah>bloo</blah>', [blah: ['blee', 'bloo']]
    }

    void testText() {
        assertMapToDom '<blah>blee</blah>', [blah: ['_TEXT_': 'blee']]
        assertMapToDom '<blah>blee<bloo>blam</bloo></blah>', [blah: [bloo: 'blam', '_TEXT_': 'blee']]
    }

    void testAttributes() {
        assertMapToDom '<blah monkey=\'see\'>blee</blah>', [blah: ['@attr:monkey': 'see', '_TEXT_': 'blee']]
    }

    void testPlural() {
        assertMapToDom '<blahs><blah>a</blah><blah>b</blah><blah>c</blah></blahs>', ['blah[s]': ['a', 'b', 'c']]
    }

    void testCdata() {
        assertMapToDom '<abc><![CDATA[xyz<monkey]]></abc>', ['abc<cdata>': 'xyz<monkey']
    }

    void testAddAttribute() {
        assertEquals(['@attr:abc': 'def'], BuilderUtil.addAttribute([:], 'abc', 'def'))
    }

    void testMakeAttribute() {
        assertEquals(['@attr:abc': 'def'], BuilderUtil.makeAttribute([abc: 'def'], 'abc'))
    }

    void testAsAttributeName() {
        assert '@attr:abc' == BuilderUtil.asAttributeName('abc')
    }

    void testPluralize(){
        assert 'test[s]' == BuilderUtil.pluralize('test[s]')
        assert 'test[s]' == BuilderUtil.pluralize('tests')
        assert 'test[s]' == BuilderUtil.pluralize('test')
    }

    void testMakePlural(){
        assertEquals(['abc[s]':'def'], BuilderUtil.makePlural([abc: 'def'], 'abc'))
        assertEquals(['abc[s]':'def'], BuilderUtil.makePlural([abcs: 'def'], 'abcs'))
        assertEquals(['abc[s]':'def'], BuilderUtil.makePlural(['abc[s]': 'def'], 'abc[s]'))
    }
}