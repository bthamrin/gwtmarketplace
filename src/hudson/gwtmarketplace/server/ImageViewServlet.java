package hudson.gwtmarketplace.server;

import hudson.gwtmarketplace.server.util.ImageUtil;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImageViewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String key = req.getParameter("key");
		if (null == key)
			resp.setStatus(404);
		else {
			byte[] data = ImageUtil.iconize(key);
			if (null == data) {
				resp.setStatus(404);
			}
			else {
				resp.getOutputStream().write(data);
			}
		}
	}
}
