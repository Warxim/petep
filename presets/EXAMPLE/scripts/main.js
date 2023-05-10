/*
 Main script
*/
// Import config script
scripter.require('config.js');

// Load PETEP utils
const PduUtils = Java.type('com.warxim.petep.util.PduUtils');
const BytesUtils = Java.type('com.warxim.petep.util.BytesUtils');

// Load Java utils
const StringJoiner = Java.type('java.util.StringJoiner');
const ArrayList = Java.type('java.util.ArrayList');

// Obtain logger
const log = scripter.getLogger();

log.info(config.messages.init);

// Register info interceptor
scripter.registerInterceptor({
    intercept: function(pdu, helper) {
		var information = '';
		
		if (config.log.type) {
			information += '\n- type: ' + pdu.getClass().getSimpleName();
		}
		
		if (config.log.destination) {
			information += '\n- destination: ' + pdu.getDestination();
		}
		
		if (config.log.proxy) {
			let proxy = pdu.getProxy();
			information += '\n- proxy: ' + proxy.getName() + ' (' + proxy.getCode() + ')';
		}
		
		if (config.log.connection) {
			let connection = pdu.getConnection();
			information += '\n- connection: ' + connection.getCode();
		}
		
		if (config.log.interceptor) {
			let interceptor = pdu.getLastInterceptor();
			information += '\n- last interceptor: ' + interceptor.getName() + ' (' + interceptor.getCode() + ')';
		}
		
		if (config.log.size) {
			information += '\n- size: ' + pdu.getSize();
		}
		
		if (config.log.tags) {
			let tagJoiner = new StringJoiner(', ');
			pdu.getTags().forEach(tagJoiner.add);
			information += '\n- tags: ' + tagJoiner.toString();
		}
		
		if (config.log.buffer) {
			information += '\n- buffer: ' + PduUtils.bufferToHexString(pdu);
		}
		
		if (information.length > 0) {
			log.info('PDU information: ' + information);
		}
		
        return true;
    }
});

// Register colorize interceptor
scripter.registerInterceptor({
    intercept: function(pdu, helper) {
		if (pdu.getProxy().getCode() !== 'petep') {
			return true;
		}
		
		for (let currentColor in config.colors) {
			let newColor = config.colors[currentColor];
			PduUtils.replace(
				pdu,
				BytesUtils.getBytes(currentColor),
				BytesUtils.getBytes(newColor)
			);
		}
		return true;
    }
});