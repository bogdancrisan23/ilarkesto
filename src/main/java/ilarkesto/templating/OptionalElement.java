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
package ilarkesto.templating;

class OptionalElement extends ATemplateElement {

	private String expression;
	private ATemplateElement contentTemplate;

	public OptionalElement(String expression, ATemplateElement contentTemplate) {
		super();
		this.expression = expression;
		this.contentTemplate = contentTemplate;
	}

	@Override
	public void onProcess() {
		if (!evalExpressionAsBoolean(expression)) return;
		contentTemplate.process(context);
	}

}