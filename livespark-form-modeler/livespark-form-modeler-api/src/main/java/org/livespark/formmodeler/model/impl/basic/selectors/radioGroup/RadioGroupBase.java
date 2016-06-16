/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.livespark.formmodeler.model.impl.basic.selectors.radioGroup;

import org.livespark.formmodeler.metaModel.FieldDef;
import org.livespark.formmodeler.model.impl.basic.selectors.SelectorFieldBase;
import org.livespark.formmodeler.model.impl.basic.selectors.SelectorOption;
import org.livespark.formmodeler.service.impl.fieldProviders.RadioGroupFieldProvider;

/**
 * @author Pere Fernandez <pefernan@redhat.com>
 */
public abstract class RadioGroupBase<T extends SelectorOption<?>> extends SelectorFieldBase<T> {

    public static final String CODE = "RadioGroup";

    public RadioGroupBase() {
        super( CODE );
    }

    @FieldDef( label = "Show options inline" )
    protected Boolean inline = Boolean.FALSE;

    public Boolean getInline() {
        return inline;
    }

    public void setInline( Boolean inline ) {
        this.inline = inline;
    }
}