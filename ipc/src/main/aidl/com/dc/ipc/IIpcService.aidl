package com.dc.ipc;

import com.dc.ipc.model.Request;
import com.dc.ipc.model.Response;

interface IIpcService {
    Response send(in Request request);
}