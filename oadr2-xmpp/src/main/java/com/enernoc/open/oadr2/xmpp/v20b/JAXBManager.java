package com.enernoc.open.oadr2.xmpp.v20b;

import javax.xml.bind.JAXBException;

/**
 * Convenience class for using JAXB with OpenADR 2.0b profile 
 * generated classes.  See {@link com.enernoc.open.oadr2.xmpp.JAXBManager} 
 * @author tnichols
 */
public class JAXBManager extends com.enernoc.open.oadr2.xmpp.JAXBManager {

    public static final String DEFAULT_JAXB_CONTEXT_PATH = 
        "openadr.model.v20b" +
        ":openadr.model.v20b.atom" +
        ":openadr.model.v20b.currency" +
        ":openadr.model.v20b.ei" +
        ":openadr.model.v20b.emix" +
        ":openadr.model.v20b.gml" +
        ":openadr.model.v20b.greenbutton" +
        ":openadr.model.v20b.power" +
        ":openadr.model.v20b.pyld" +
        ":openadr.model.v20b.siscale" +
        ":openadr.model.v20b.strm" +
        ":openadr.model.v20b.xcal" +
        ":openadr.model.v20b.xmldsig" +
        ":openadr.model.v20b.xmldsig11";
    
    public JAXBManager() throws JAXBException {
        super(DEFAULT_JAXB_CONTEXT_PATH);
    }
    
    @Override
    protected OADR2NamespacePrefixMapper createPrefixMapper() {
        return new OADR2NamespacePrefixMapper();
    }
}
