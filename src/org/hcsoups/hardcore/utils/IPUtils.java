package org.hcsoups.hardcore.utils;

import java.util.regex.Pattern;

public class IPUtils {

    static Pattern IP_PATTERN = Pattern.compile("([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3})");

	
	static public boolean isValidIP(String ip) {
	     return IP_PATTERN.matcher(ip).matches();
	}
	
}