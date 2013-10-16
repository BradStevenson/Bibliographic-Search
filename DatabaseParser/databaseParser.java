package databaseParserPackage;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class databaseParser {
    public static void main (String argv[]) {
	/*
	 * Setup MySQL here when we have it hosted.
	 */
        try {
            // Setting up our SAXParser
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            String PATH_TO_XML_FILE = "dblp.xml";
            
            DefaultHandler handler = new DefaultHandler() {
            
        	// Adding all the fields we wish to extract.
                boolean bAuthor = false;
                boolean bTitle = false;
                boolean bPages = false;
                boolean bYear = false;
                boolean bVolume = false;
                boolean bJournal = false;
                boolean bNumber = false;
                boolean bEE = false;
                boolean bURL = false;
                
                // Scans each section and marks which properties it has.
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            		System.out.println("Start element: " + qName);
            		if (qName.equalsIgnoreCase("AUTHOR")) {
            		    bAuthor = true;
            		}
            		if (qName.equalsIgnoreCase("TITLE")) {
            		    bTitle = true;
            		}
            		if (qName.equalsIgnoreCase("PAGES")) {
            		    bPages = true;
            		}
            		if (qName.equalsIgnoreCase("YEAR")) {
            		    bYear = true;
            		}
            		if (qName.equalsIgnoreCase("VOLUME")) {
            		    bVolume = true;
            		}
            		if (qName.equalsIgnoreCase("JOURNAL")) {
            		    bJournal = true;
            		}
            		if (qName.equalsIgnoreCase("NUMBER")) {
            		    bNumber = true;
            		}
            		if (qName.equalsIgnoreCase("EE")) {
            		    bEE = true;
            		}
            		if (qName.equalsIgnoreCase("URL")) {
            		    bURL = true;
            		}
            		
                }
          
                public void endElement(String uri, String localName, String qName) throws SAXException {
            		System.out.println("End element: " + qName);
                }
                // Prints out the values for each property which is marked true.
                public void characters(char ch[], int start, int length) throws SAXException {
                    if (bAuthor) {
                	System.out.println("Author: " + new String(ch, start, length));
                	// This is where we add to MySQL 
                	
                	bAuthor = false; // Mark false for next run.
                    }
                    if (bTitle) {
                	System.out.println("Title: " + new String(ch, start, length));
                	bTitle = false;
                    }
                    if (bPages) {
                	System.out.println("Pages: " + new String(ch, start, length));
                	bPages = false;
                    }
                    if (bYear) {
                	System.out.println("Year: " + new String(ch, start, length));
                	bYear = false;
                    }
                    if (bVolume) {
                	System.out.println("Volume: " + new String(ch, start, length));
                	bVolume = false;
                    }
                    if (bJournal) {
                	System.out.println("Journal: " + new String(ch, start, length));
                	bAuthor = false;
                    }
                    if (bNumber) {
                	System.out.println("Number: " + new String(ch, start, length));
                	bNumber = false;
                    }
                    if (bEE) {
                	System.out.println("EE: " + new String(ch, start, length));
                	bEE = false;
                    }
                    if (bURL) {
                	System.out.println("URL: " + new String(ch, start, length));
                	bURL = false;
                    }
                }
            };
            
            saxParser.parse(PATH_TO_XML_FILE, handler);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
