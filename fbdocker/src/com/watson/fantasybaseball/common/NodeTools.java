package com.watson.fantasybaseball.common;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeTools {
    /**
     * Method sorts any NodeList by provided attribute.
     * @param nl NodeList to sort
     * @param attributeName attribute name to use
     * @param asc true - ascending, false - descending
     * @param B class must implement Comparable and have Constructor(String) - e.g. Integer.class , BigDecimal.class etc
     * @return Array of Nodes in required order
     */
    public static Node[] sortNodes(NodeList nl, String tag, boolean asc, Class<? extends Comparable> B)
    {        
        class NodeComparator<T> implements Comparator<T>
        {
            @Override
            public int compare(T a, T b)
            {
                int ret;
                Comparable bda = null, bdb = null;
                try{
                    Constructor bc = B.getDeclaredConstructor(String.class);
                    bda = (Comparable)bc.newInstance(((Element)a).getElementsByTagName(tag).item(0).getTextContent().trim());
                    bdb = (Comparable)bc.newInstance(((Element)b).getElementsByTagName(tag).item(0).getTextContent().trim());
                }
                catch(Exception e)
                {
                    return 0; // yes, ugly, i know :)
                }
                ret = bda.compareTo(bdb);
                return asc ? ret : -ret; 
            }
        }

        List<Node> x = new ArrayList<>();
        for(int i = 0; i < nl.getLength(); i++)
        {
            x.add(nl.item(i));
        }
        Node[] ret = new Node[x.size()];
        ret = x.toArray(ret);
        Arrays.sort(ret, new NodeComparator<Node>());
        return ret;
    }
}