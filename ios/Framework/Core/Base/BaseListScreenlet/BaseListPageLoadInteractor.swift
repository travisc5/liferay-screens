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
import UIKit


public class BaseListPageLoadInteractor: ServerReadOperationInteractor {

	public let page: Int
	public let computeRowCount: Bool

	public var resultAllPagesContent: [AnyObject?]?
	public var resultPageContent: [AnyObject]?
	public var resultRowCount: Int?


	public init(screenlet: BaseListScreenlet, page: Int, computeRowCount: Bool) {
		self.page = page
		self.computeRowCount = computeRowCount

		super.init(screenlet: screenlet)
	}

	override public func createOperation() -> LiferayPaginationOperation {
		assertionFailure("createOperation must be overriden")

		return LiferayPaginationOperation(
				startRow: 0,
				endRow: 0,
				computeRowCount: self.computeRowCount)
	}

	override public func completedOperation(op: ServerOperation) {
		if op.lastError != nil {
			return
		}

		if let pageOp = op as? LiferayPaginationOperation {
			processLoadPageResult(pageOp.resultPageContent ?? [], rowCount: pageOp.resultRowCount)
		}
	}

	private func processLoadPageResult(serverRows: [[String:AnyObject]], rowCount: Int?) {
		let screenlet = self.screenlet as! BaseListScreenlet
		let baseListView = screenlet.screenletView as! BaseListView

		let actualRowCount = rowCount ?? baseListView.rowCount

		let convertedRows = serverRows.map() { self.convertResult($0) }

		var allRows = Array<AnyObject?>(count: actualRowCount, repeatedValue: nil)

		for (index, row) in enumerate(baseListView.rows) {
			allRows[index] = row
		}

		var offset = screenlet.firstRowForPage(page)

		// last page could be incomplete
		if offset >= actualRowCount {
			offset = actualRowCount - 1
		}

		for (index, row) in enumerate(convertedRows) {
			allRows[offset + index] = row
		}

		self.resultRowCount = actualRowCount
		self.resultPageContent = convertedRows
		self.resultAllPagesContent = allRows
	}

	public func convertResult(serverResult: [String:AnyObject]) -> AnyObject {
		assertionFailure("convert(serverResult) must be overriden")
		return 0
	}

}
