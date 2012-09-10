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
package ilarkesto.webapp;

import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AServlet<A extends AWebApplication, S extends AWebSession> extends HttpServlet {

	private static Log log = Log.get(AServlet.class);

	protected A webApplication;

	protected void onGet(RequestWrapper<S> req) throws IOException {
		req.sendErrorNoContent();
	}

	protected void onPost(RequestWrapper<S> req) throws IOException {
		req.sendErrorNoContent();
	}

	protected void onInit(ServletConfig config) throws ServletException {}

	@Override
	protected final void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
			throws ServletException, IOException {
		RequestWrapper<S> req = new RequestWrapper<S>(httpRequest, httpResponse);
		if (!init(req)) return;
		try {
			onGet(req);
		} catch (Throwable ex) {
			handleError(ex, req);
		}
	}

	@Override
	protected final void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
			throws ServletException, IOException {
		RequestWrapper<S> req = new RequestWrapper<S>(httpRequest, httpResponse);
		if (!init(req)) return;
		try {
			onPost(req);
		} catch (Throwable ex) {
			handleError(ex, req);
		}
	}

	private boolean init(RequestWrapper<S> req) throws IOException {
		if (webApplication == null) {
			req.sendErrorServiceUnavailable("Application not started yet");
			return false;
		}
		if (webApplication.isShuttingDown()) {
			req.sendErrorServiceUnavailable(webApplication.getApplicationLabel() + " shutting down");
			return false;
		}
		if (webApplication.isStartupFailed()) {
			req.sendErrorServiceUnavailable(webApplication.getApplicationLabel() + " startup failed");
			return false;
		}
		req.getSession().getContext().bindCurrentThread();
		return true;
	}

	private void handleError(Throwable ex, RequestWrapper<S> req) {
		log.info("request caused error:", req, ex);
		req.sendErrorInternal(Str.format(ex));
	}

	@Override
	public final void init(ServletConfig config) throws ServletException {
		super.init(config);
		onInit(config);
		webApplication = (A) AWebApplication.get();
	}

}
