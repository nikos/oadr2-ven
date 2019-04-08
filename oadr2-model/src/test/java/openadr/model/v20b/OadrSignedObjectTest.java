package openadr.model.v20b;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import openadr.model.v20b.ei.ReadingTypeEnumeratedType;
import openadr.model.v20b.ei.ReportSpecifier;
import openadr.model.v20b.ei.SpecifierPayload;
import openadr.model.v20b.xcal.DurationPropType;
import openadr.model.v20b.xcal.DurationValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Some sanity checking for our JAXB-generated models
 *
 * @author <a href='mailto:tnichols@enernoc.com'>Thom Nichols</a>
 */
public class OadrSignedObjectTest {

    private Marshaller marshaller;
    private DatatypeFactory xmlDataTypeFac;

    private SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    private Validator validator;

    private ObjectFactory of = new ObjectFactory();

    @Before
    public void setup() throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(
                "openadr.model.v20b:" +
                        "openadr.model.v20b.atom:" +
                        "openadr.model.v20b.currency:" +
                        "openadr.model.v20b.ei:" +
                        "openadr.model.v20b.emix:" +
                        "openadr.model.v20b.gml:" +
                        "openadr.model.v20b.greenbutton:" +
                        "openadr.model.v20b.power:" +
                        "openadr.model.v20b.pyld:" +
                        "openadr.model.v20b.siscale:" +
                        "openadr.model.v20b.strm:" +
                        "openadr.model.v20b.xcal:" +
                        "openadr.model.v20b.xmldsig:" +
                        "openadr.model.v20b.xmldsig11");

        this.marshaller = jaxbContext.createMarshaller();
        xmlDataTypeFac = DatatypeFactory.newInstance();

        Schema schema = sf.newSchema(getClass().getResource("/schema/2.0b/oadr_20b.xsd"));
        this.validator = schema.newValidator();
    }

    @Test
    public void testSerialize() throws Exception {
        final Duration duration = xmlDataTypeFac.newDuration(true, 0, 0, 0, 0, 5, 0);

        OadrSignedObject payload = new OadrSignedObject()
                .withOadrCreateReport(new OadrCreateReport()
                        .withRequestID("1234")
                        .withVenID("Vtn-1234")
                        .withSchemaVersion("2.0b")
                        .withOadrReportRequests(new OadrReportRequest()
                                .withReportRequestID("request-1234")
                                .withReportSpecifier(new ReportSpecifier()
                                        .withReportSpecifierID("1234")
                                        .withGranularity(new DurationPropType(new DurationValue(duration.toString())))
                                        .withReportBackDuration(new DurationPropType(new DurationValue(duration.toString())))
                                        .withSpecifierPayloads(new SpecifierPayload()
                                                .withRID("report 1234")
                                                .withReadingType(ReadingTypeEnumeratedType.DIRECT_READ.value())
                                                .withItemBase(of.createPulseCount(new PulseCountType()
                                                        .withItemDescription("pulse count")
                                                        .withItemUnits("count")
                                                        .withPulseFactor(.01f)))))));

        assertEquals("1234", payload.getOadrCreateReport().getRequestID());

        StringWriter out = new StringWriter();
        this.marshaller.marshal(payload, out);

        assertNotNull(out.toString());

        assertTrue(out.toString().length() > 0);

        assertEquals(.01f, ((PulseCountType) payload.getOadrCreateReport()
                .getOadrReportRequests().get(0)
                .getReportSpecifier().getSpecifierPayloads().get(0)
                .getItemBase().getValue()).getPulseFactor(), 0);


        assertEquals(0, validate(out.toString()));
    }


    private int validate(String doc) throws IOException, SAXException {
        ErrorCollector errorCollector = new ErrorCollector();
        validator.setErrorHandler(errorCollector);
        validator.validate(new StreamSource(new StringReader(doc)));

        return errorCollector.errors.size();
    }

    class ErrorCollector extends DefaultHandler {
        List<SAXParseException> errors = new ArrayList<>();

        @Override
        public void error(SAXParseException e) {
            System.out.println("SAX Parse error (" + e.getLineNumber() + "): " + e.getMessage());
            errors.add(e);
        }
    }
}
