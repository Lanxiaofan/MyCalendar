/**
 * Copyright (C) 2014 Luki(liulongke@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.widget.calendar.Bean;

import java.io.Serializable;

/**
 * 这里为何实现Serializable而不是Parcelable:lc
 * Java的序列化机制是通过在运行时判断类的serialVersionUID来验证版本一致性的。
 * 在进行反序列化时，JVM会把传来的字节流中的serialVersionUID与本地相应实体（类）
 * 的serialVersionUID进行比较，如果相同就认为是一致的，可以进行反序列化，
 * 否则就会出现序列化版本不一致的异常。(InvalidCastException)
 */
public class BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1653014188878954360L;
}
