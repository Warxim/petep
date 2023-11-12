scripter.registerInterceptor({
    intercept: function(pdu, helper) {
        const size = pdu.getSize();
        if (size != 256) {
            return true;
        }

        const buffer = pdu.getBuffer();
        for (let i = 0; i < 256; ++i) {
            buffer[i] = i;
        }
        return true;
    }
});
