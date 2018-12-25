package com.bazzi.core.ds;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;

/**
 * 进入事务时进行相应的操作
 * 
 * @author PanJianzang
 *
 */
public class DataSourceTransactionHandler extends DataSourceTransactionManager {
	private static final long serialVersionUID = -2238478723841075608L;

	/**
	 * 进入事务流程时，读写都切回主库
	 */
	protected void doBegin(Object transaction, TransactionDefinition definition) {
		DataSourceHandler.switchToMaster();
		super.doBegin(transaction, definition);
	}

	/**
	 * 重置
	 */
	protected void doCleanupAfterCompletion(Object transaction) {
		super.doCleanupAfterCompletion(transaction);
		DataSourceHandler.reset();
	}

}
