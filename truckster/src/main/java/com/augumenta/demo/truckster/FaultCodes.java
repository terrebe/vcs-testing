/*
 * Copyright (c) 2012-2016 Augumenta Ltd. All rights reserved.
 *
 * This source code file is furnished under a limited license and may be used or
 * copied only in accordance with the terms of the license. Except as permitted
 * by the license, no part of this source code file may be  reproduced, stored in
 * a retrieval system, or transmitted, in any form or by  any means, electronic,
 * mechanical, recording, or otherwise, without the prior written permission of
 * Augumenta.
 *
 * This source code file contains proprietary information that is protected by
 * copyright. Certain parts of proprietary information is patent protected. The
 * content herein is furnished for informational use only, is subject to change
 * without notice, and should not be construed as a commitment by Augumenta.
 * Augumenta assumes no responsibility or liability for any errors or
 * inaccuracies that may appear in the informational content contained herein.
 * This source code file has not been thoroughly tested under all conditions.
 * Augumenta, therefore, does not guarantee or imply its reliability,
 * serviceability, or function.
 *
 */

package com.augumenta.demo.truckster;

/**
 * FaultCodes provides the code, description (short description) and the url (as android_asset)
 * for the FaultCodes[] array.
 */
public class FaultCodes {

	public static final FaultCode[] codes = new FaultCode[] {
			new FaultCode("P0302", "Cylinder #2 Misfire", "file:///android_asset/fault_codes/1-P0302.html"),
			new FaultCode("P0325", "Knock Sensor Circuit Malfunction", "file:///android_asset/fault_codes/2-P0325.html"),
			new FaultCode("P0400", "Exhaust Gas Recirculation Flow Malfunction", "file:///android_asset/fault_codes/3-P0400.html"),
	};

	public static class FaultCode {
		public String code;
		public String description;
		public String uri;

		public FaultCode(String code, String description, String uri) {
			this.code = code;
			this.description = description;
			this.uri = uri;
		}
	}
}
