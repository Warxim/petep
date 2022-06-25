const PduUtils = Java.type('com.warxim.petep.util.PduUtils');
const BytesUtils = Java.type('com.warxim.petep.util.BytesUtils');

scripter.registerInterceptor({
    counter: 0,
    intercept: function(pdu, helper) {
        print("In processor Y got (" + (++this.counter) + "): " + pdu);
        PduUtils.replace(pdu, BytesUtils.getBytes("world"), BytesUtils.getBytes("PETEP"));
        return true;
    }
});

scripter.registerInterceptor({
    counter: 0,
    intercept: function(pdu, helper) {
        print("In processor X got (" + (++this.counter) + "): " + pdu);
        PduUtils.replace(pdu, BytesUtils.getBytes("Hello"), BytesUtils.getBytes("Hi"));
        return true;
    }
});