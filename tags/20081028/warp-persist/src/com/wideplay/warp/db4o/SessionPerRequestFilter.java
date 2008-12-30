/**
 * Copyright (C) 2008 Wideplay Interactive.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wideplay.warp.db4o;

import com.wideplay.warp.persist.SessionFilter;
import net.jcip.annotations.ThreadSafe;

/**
 * <p>
 * Apply this filter in web.xml to enable the HTTP Request unit of work.
 * </p>
 * 
 * @author Jeffrey Chung (jeffreymchung@gmail.com)
 * @see com.wideplay.warp.persist.UnitOfWork
 * @deprecated use {@link com.wideplay.warp.persist.SessionFilter}
 */
@Deprecated
@ThreadSafe
public class SessionPerRequestFilter extends SessionFilter {}