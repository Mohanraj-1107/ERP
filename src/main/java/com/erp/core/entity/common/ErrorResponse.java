//$Id$
package com.erp.core.entity.common;

import java.time.Instant;

public record ErrorResponse (
	String errorMessage,
	Integer errorCode,
	String path,
	Instant timestamp
){}