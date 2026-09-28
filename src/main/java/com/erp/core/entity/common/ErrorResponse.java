//$Id$
package com.erp.core.entity.common;

import java.time.Instant;

public record ErrorResponse (
	String errorMessgae,
	Integer errorCode,
	String path,
	Instant timestamp
){}
