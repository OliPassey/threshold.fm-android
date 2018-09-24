package anywheresoftware.b4a.objects;

import anywheresoftware.b4a.AbsObjectWrapper;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.RaisesSynchronousEvents;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.BA.Version;
import anywheresoftware.b4a.keywords.StringBuilderWrapper;
import anywheresoftware.b4a.objects.collections.List;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

@Version(1.11f)
@ShortName("SaxParser")
public class SaxParser {
    public List Parents = new List();
    private BA ba;
    private String eventName;
    private SAXParser sp;

    @ShortName("Attributes")
    public static class AttributesWrapper extends AbsObjectWrapper<Attributes> {
        public int getSize() {
            return ((Attributes) getObject()).getLength();
        }

        public String GetName(int Index) {
            return ((Attributes) getObject()).getLocalName(Index);
        }

        public String GetValue(int Index) {
            return ((Attributes) getObject()).getValue(Index);
        }

        public String GetValue2(String Uri, String Name) {
            String r = ((Attributes) getObject()).getValue(Uri, Name);
            return r == null ? "" : r;
        }
    }

    private class MyHandler extends DefaultHandler {
        private AttributesWrapper aw;
        private final String endEvent;
        private StringBuilder sb = new StringBuilder();
        private StringBuilderWrapper sbw;
        private final String startEvent;

        public MyHandler() {
            this.startEvent = SaxParser.this.eventName + "_" + "startelement";
            this.endEvent = SaxParser.this.eventName + "_" + "endelement";
            this.sbw = new StringBuilderWrapper();
            this.aw = new AttributesWrapper();
            SaxParser.this.Parents.Initialize();
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            this.sb.setLength(0);
            this.aw.setObject(attributes);
            SaxParser.this.ba.raiseEvent2(null, true, this.startEvent, false, uri, localName, this.aw);
            SaxParser.this.Parents.Add(localName);
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            this.sb.append(ch, start, length);
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            SaxParser.this.Parents.RemoveAt(SaxParser.this.Parents.getSize() - 1);
            this.sbw.setObject(this.sb);
            SaxParser.this.ba.raiseEvent2(null, true, this.endEvent, false, uri, localName, this.sbw);
        }
    }

    public static void LIBRARY_DOC() {
    }

    public void Initialize(BA ba) throws ParserConfigurationException, SAXException {
        this.sp = SAXParserFactory.newInstance().newSAXParser();
        this.ba = ba;
    }

    @RaisesSynchronousEvents
    public void Parse(InputStream InputStream, String EventName) throws ParserConfigurationException, SAXException, IOException {
        parse(new InputSource(InputStream), EventName);
    }

    private void parse(InputSource in, String EventName) throws SAXException, IOException {
        this.eventName = EventName.toLowerCase(BA.cul);
        MyHandler m = new MyHandler();
        XMLReader xr = this.sp.getXMLReader();
        xr.setContentHandler(m);
        xr.parse(in);
    }

    @RaisesSynchronousEvents
    public void Parse2(Reader TextReader, String EventName) throws ParserConfigurationException, SAXException, IOException {
        parse(new InputSource(TextReader), EventName);
    }
}
