/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingsphere.core.executor.fixture;

import io.shardingsphere.core.event.ShardingEventType;
import io.shardingsphere.core.executor.event.ExecutionEvent;
import io.shardingsphere.core.executor.event.OverallExecutionEvent;
import io.shardingsphere.core.executor.event.SQLExecutionEvent;
import io.shardingsphere.core.executor.threadlocal.ExecutorExceptionHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExecutorTestUtil {
    
    /**
     * Listen event.
     * 
     * @param eventCaller event caller
     * @param event execution event
     */
    public static void listen(final EventCaller eventCaller, final ExecutionEvent event) {
        if (event instanceof SQLExecutionEvent) {
            SQLExecutionEvent sqlExecutionEvent = (SQLExecutionEvent) event;
            eventCaller.verifyDataSource(sqlExecutionEvent.getDataSource());
            eventCaller.verifySQL(sqlExecutionEvent.getSqlUnit().getSql());
            eventCaller.verifyParameters(sqlExecutionEvent.getParameters());
            eventCaller.verifyEventExecutionType(sqlExecutionEvent.getEventType());
            
        } else if (event instanceof OverallExecutionEvent) {
            eventCaller.verifySQLType(((OverallExecutionEvent) event).getSqlType());
            eventCaller.verifyIsParallelExecute(((OverallExecutionEvent) event).isParallelExecute());
        }
        if (ShardingEventType.EXECUTE_FAILURE == event.getEventType()) {
            eventCaller.verifyException(event.getException());
        }
    }
    
    /**
     * Clear thread local.
     * 
     * @throws ReflectiveOperationException reflective operation exception
     */
    public static void clear() throws ReflectiveOperationException {
        Field field = ExecutorExceptionHandler.class.getDeclaredField("IS_EXCEPTION_THROWN");
        field.setAccessible(true);
        ((ThreadLocal) field.get(ExecutorExceptionHandler.class)).remove();
    }
}
