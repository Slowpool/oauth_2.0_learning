package com.swetlokognatsk.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

class ClientApplicationTests {

	private static String customPercentEncoding(final String source) {
		return source.replace(":", "%3A").replace("/", "%2F").replace("?", "%3F").replace("#", "%23").replace("&", "%26").replace("=", "%3D");
	}

	// does not work. UriComponentsBuilder encodes only whitespace characters via percent encoding
	@Test
	public void uriEncodingViaUriComponentsBuilder() {
		var redirectUri = "https://localhost/callback?key1=val1&key2=value2#some_pointer";
		var uriBuilder = UriComponentsBuilder.fromUriString(redirectUri);

		uriBuilder.encode(StandardCharsets.UTF_8);

		var encodedUri = uriBuilder.build().toUriString();
		var expectedUri = customPercentEncoding(redirectUri);
		assertEquals(expectedUri, encodedUri);
	}

	@Test
	public void uriEncodingViaUriUtils() {
		var redirectUri = "https://localhost/callback?key1=val1&key2=value2#some_pointer";

		var encodedUri = UriUtils.encode(redirectUri, StandardCharsets.UTF_8);

		var expectedUri = customPercentEncoding(redirectUri);
		assertEquals(expectedUri, encodedUri);
	}

	@Test
	public void stripLeading() {
		var someString = "     bazinga     ";

		var strippedString = someString.stripLeading();

		assertEquals("bazinga     ", strippedString);
	}

	@Test
	public void stripTrailing() {
		var someString = "     bazinga     ";

		var strippedString = someString.stripTrailing();

		assertEquals("     bazinga", strippedString);
	}

	@Test
	public void stripBothViaStripLeadingAndTrailingCombo() {
		var someString = "     bazinga     ";

		var strippedString = someString.stripLeading().stripTrailing();

		assertEquals("bazinga", strippedString);
	}

	@Test
	public void stripBothViaTrim() {
		var someString = "     bazinga     ";

		// trim considered legacy because it is not unicode-aware
		var strippedString = someString.trim();

		assertEquals("bazinga", strippedString);
	}

	@Test
	public void stripLeadingTab() {
		var someString = "\tbazinga\t";

		var strippedString = someString.stripLeading();

		assertEquals("bazinga\t", strippedString);
	}

	@Test
	public void stripTrailingTab() {
		var someString = "\tbazinga\t";

		var strippedString = someString.stripTrailing();

		assertEquals("\tbazinga", strippedString);
	}

	@Test
	public void stripTrimTab() {
		var someString = "\tbazinga\t";

		var strippedString = someString.trim();

		assertEquals("bazinga", strippedString);
	}
}
