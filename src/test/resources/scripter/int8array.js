scripter.registerInterceptor({
    intercept: function(pdu, helper) {
        const newBuffer = new Int8Array(256);

        for (let i = 0; i < 256; ++i) {
            newBuffer[i] = i;
        }

        pdu.setBuffer(newBuffer, newBuffer.length);
        return true;
    }
});
