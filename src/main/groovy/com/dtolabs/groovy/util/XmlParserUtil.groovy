package com.dtolabs.groovy.util

import groovy.util.slurpersupport.GPathResult

/*
* Copyright 2012 DTO Labs, Inc. (http://dtolabs.com)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

/*
 * Copied from: https://github.com/dtolabs/rundeck/blob/development/rundeckapp/src/groovy/XmlParserUtil.groovy
 */

/**
* XmlParserUtil converts a groovy Node structure into an object structure using Maps, and Lists.
*
* @author Greg Schueler <a href="mailto:greg@dtosolutions.com">greg@dtosolutions.com</a>
* @created Jan 27, 2011 6:48:20 PM
*
*/

public class XmlParserUtil {
    Node data

    /**
     * Create an XmlParserUtil with a Node instance.
     */
    public XmlParserUtil(Node data) {
        this.data = data
    }

    /**
     * Return a Map of [name: contents] for the node data.
     */
    Map toMap() {
        return [(data.name()): toObject()]
    }
    /**
     * Return the object from the node using {@link #toObject(Node)}
     */
    Object toObject() {
        return XmlParserUtil.toObject(data)
    }

    /**
     * Return the node data converted into a standard Java Collection or String object.
     * The result will be a Map of key,value pairs if the Node was complex, and a string value if the node has no attributes or sub-elements. 
     * Empty elements will return an empty string. The original node's element name will not be included in the map result.
     * <p>For complex XML Elements, a Map will be returned:</p>
     * <ul>
     * <li>Text content will have a key of '&lt;text&gt;'</li>
     * <li>Attributes will be keyed by the attribute itself (may be a QName)</li>
     * <li>Both text and attribute values will be parsed as Integers or Booleans if possible, otherwise the value will be a String</li>
     * <li>Child Elements will be keyed by element name (may be a QName), and the value will be:
     *   <ol>
     *   <li>A list of objects recursively created by this method, if multiple child elements of the same name exist</li>
     *    <li>An object created by this method if only a single child element with the name exists</li>
     *   </ol>
     * </li> 
     * </ul>
     */
    static Object toObject(Node data) {
        if (null == data) {
            return null
        }
        def childs = data.value()
        def text = data.text()
        def attrs = data.attributes()
        def map = [:]
        if (data.text()) {
            map['<text>'] = XmlParserUtil.analyzeText(text)
        }
        if (attrs) {
            attrs.keySet().each{
                map.put(it,analyzeText(attrs[it]))
            }
        }
        if (null != childs && childs instanceof Collection) {
            childs.each {gp ->
                if (gp instanceof Node) {
                    if (null != map[gp.name()] && !(map[gp.name()] instanceof Collection)) {
                        def v = map[gp.name()]
                        map[gp.name()] = [v, toObject(gp)]
                    } else if (map[gp.name()] instanceof Collection) {
                        map[gp.name()] << toObject(gp)
                    } else {
                        map[gp.name()] = toObject(gp)
                    }
                }
            }
        }
        if (1 == map.size() && null!=map['<text>']) {
            return map['<text>']
        }else if(0==map.size()){
            return ''
        }
        return map
    }
    /**
     * Returns an integer if the string is all digits, a boolean if 
     * the string is 'true' or 'false', and the original String otherwise.
     */
    static Object analyzeText(String text){
        if(text=~/^\d+$/){
            return Integer.parseInt(text)
        }else if(text=~/^(?i:true|false)$/){
            return Boolean.parseBoolean(text)
        }
        return text
    }
}