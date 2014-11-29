package org.ryangray.airfare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class CopyOfKayakSite {
	
    public static void main(String[] args) throws Exception {
    	
    	getElements();
    	
    }
    
    public static void getElements() throws Exception {
        final WebClient webClient = new WebClient();
        String url = "http://www.kayak.com/#/flights/MSP-MUC/2013-06-25/2013-07-09";
        HtmlPage page = getSafeHtmlPage(webClient, url);
        
        final HtmlDivision div = page.getHtmlElementById("content_div");
        test(div);
        Iterable<DomElement> flights = div.getChildElements();
        for (DomElement domElement : flights) {
			findByClass(domElement, "pricerange");
//        	DomNode priceRange = (DomNode) domElement.getByXPath(xpath);
//        	System.out.println(priceRange.getNodeValue());
		}
        FileWriter kayakTestOutput = new FileWriter("F:\\users\\ryan\\desktop\\kayaktest.txt");
        kayakTestOutput.append(page.asXml());
        kayakTestOutput.close();
        webClient.closeAllWindows();
    }

	private static HtmlPage getSafeHtmlPage(WebClient webClient, String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		long end = System.currentTimeMillis() + 30000;
        HtmlPage page = null;
        while (System.currentTimeMillis() < end) {
            // Browsers which render content (such as Firefox and IE) return "RenderedWebElements"
        	page = webClient.getPage("http://www.kayak.com/#/flights/MSP-MUC/2013-06-25/2013-07-09");

            // If results have been returned, the results are displayed in a drop down.
            if ("complete".equals(page.getReadyState())) {
            	break;
            }
        }
        return page;
	}

	private static String test(HtmlDivision div) {
		NamedNodeMap test1 = div.getAttributes();
		List<?> test2 = div.getByXPath("//div");
		Iterable<DomElement> test3 = div.getChildElements();
		DomNodeList<DomNode> test4 = div.getChildNodes();
		Iterable<DomNode> test5 = div.getChildren();
		DomNodeList<HtmlElement> test6 = div.getElementsByTagName("div");
		List<?> test7 = div.getByXPath("..//*");
		System.out.println(test7.size());
//		HtmlElement test7 = div.getElementById("priceAnchor189");
//		HtmlElement test8 = div.getElementById("priceAnchor*");
//		for (HtmlElement htmlElement : nodes) {
//			if (!htmlElement.hasAttributes() || !htmlElement.hasChildNodes()) {
//				continue;
//			}
//			NamedNodeMap nodeAttributes = htmlElement.getAttributes();
//			Node nodeClassAttribute = nodeAttributes.getNamedItem("class");
//			if ("pricerange".equals(nodeClassAttribute.getNodeValue())) {
//				System.out.println(htmlElement.getNodeValue());
//				return htmlElement.getNodeValue();
//			}
//		}
		return null;
	}

	private static void findByClass(DomElement domElement, String string) {
		if (domElement.hasChildNodes()) {
			DomNodeList<DomNode> children = domElement.getChildNodes();
			for (DomNode domNode : children) {
//				find
			}
		}
		
	}
    
}