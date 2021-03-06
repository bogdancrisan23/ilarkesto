/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package ilarkesto.core.forms;

import ilarkesto.core.base.Str;

public class FormFieldsStatus {

	private int total;
	private int valuesSet;
	private int valuesNotSet;
	private int mandatoryValuesSet;
	private int mandatoryValuesNotSet;

	public void addField(Object value, boolean mandatory) {
		addField(isValueSet(value), mandatory);
	}

	private boolean isValueSet(Object value) {
		if (value == null) return false;
		if (value instanceof String) return !Str.isBlank((String) value);
		return true;
	}

	public void addField(boolean valueSet, boolean mandatory) {
		total++;
		if (valueSet) {
			valuesSet++;
			if (mandatory) mandatoryValuesSet++;
		} else {
			valuesNotSet++;
			if (mandatory) mandatoryValuesNotSet++;
		}
	}

	public int getTotal() {
		return total;
	}

	public int getValuesSet() {
		return valuesSet;
	}

	public int getValuesNotSet() {
		return valuesNotSet;
	}

	public int getMandatoryValuesNotSet() {
		return mandatoryValuesNotSet;
	}

	public int getMandatoryValuesSet() {
		return mandatoryValuesSet;
	}

	public boolean containsNotSetMandatory() {
		return mandatoryValuesNotSet > 0;
	}

	public boolean isEmpty() {
		return valuesSet == 0;
	}

	public boolean isAllSet() {
		return valuesSet == total;
	}

	@Override
	public String toString() {
		return getValuesSet() + "/" + getTotal();
	}

	public boolean containsMandatory() {
		return mandatoryValuesSet > 0 || mandatoryValuesNotSet > 0;
	}

}