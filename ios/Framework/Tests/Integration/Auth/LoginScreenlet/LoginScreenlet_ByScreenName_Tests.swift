/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
import XCTest


class LoginScreenlet_ByScreenName_Tests: BaseLoginScreenletTestCase {

	func test_Successful() {
		scenario("LoginScreenlet by screenName should work") {
			given("a configured login screenlet") {
				with("auth method set to screenName") {
					self.screenlet!.basicAuthMethod = BasicAuthMethod.ScreenName.rawValue
				}
				and("screenName and password entered by the user") {
					self.screenlet!.viewModel.userName = "test"
					self.screenlet!.viewModel.password = "test"
				}
				and("server returns ok") {
					mockServer.stubService("get-user-by-screen-name",
							withResult:mockServer.loginOK())
				}
			}
			when("the request is sent and the response is received") {
				self.screenlet!.delegate = TestLoginScreenletDelegate() { result in
					done("login response received", withResult: result)
				}
				self.screenlet!.performDefaultAction()
			}
			eventually("the state of the screenlet should be consistent", {result in
				assertThat("the error should be nil") {
					XCTAssertFalse(result is NSError)
				}
				assertThat("the attributes should be populated") {
					XCTAssertTrue(result is [String:AnyObject])

					let attrs = result as! [String:AnyObject]

					XCTAssertTrue(attrs.count > 0)
					XCTAssertNotNil(attrs["screenName"])
					XCTAssertEqual("test", attrs["screenName"] as! String)
				}
				assertThat("the session should be established") {
					XCTAssertTrue(SessionContext.hasSession)
				}
				assertThat("the current user name should be the screenName") {
					XCTAssertNotNil(SessionContext.currentBasicUserName)
					XCTAssertEqual("test", SessionContext.currentBasicUserName!)
				}
				assertThat("the current password should be available") {
					XCTAssertNotNil(SessionContext.currentBasicPassword)
					XCTAssertEqual("test", SessionContext.currentBasicPassword!)
				}
			},
			.TestAndWaitFor("login response received", self))
		}
	}

}
