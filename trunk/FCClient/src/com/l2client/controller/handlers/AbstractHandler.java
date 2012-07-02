package com.l2client.controller.handlers;

import com.l2client.model.network.ClientFacade;

/**
 * Handler base class for all handler with need to access to the @see ClientFacade
 *
 */
public abstract class AbstractHandler {
    private ClientFacade clientFacade;

    public ClientFacade getClientFacade() {
        return clientFacade;
    }

    public void setClientFacade(ClientFacade f) {
        clientFacade = f;
    }
}
