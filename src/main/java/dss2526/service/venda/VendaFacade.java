package dss2526.service.venda;

import dss2526.service.base.BaseFacade;

public class VendaFacade extends BaseFacade implements IVendaFacade {

    private static VendaFacade instance;

    private VendaFacade() {}

    public static synchronized VendaFacade getInstance() {
        if (instance == null) {
            instance = new VendaFacade();
        }
        return instance;
    }
}