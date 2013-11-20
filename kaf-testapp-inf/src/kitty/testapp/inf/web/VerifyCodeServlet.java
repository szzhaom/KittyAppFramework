package kitty.testapp.inf.web;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class VerifyCodeServlet extends HttpServlet {
	private static final long serialVersionUID = -6137320789653548867L;
	// private static final String CONTENT_TYPE = "text/html; charset=gb2312";
	// 设置字母的大小,大小
	private Font mFont = new Font("Times New Roman", Font.PLAIN, 17);

	public void init() throws ServletException {
		super.init();
	}

	Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		// 表明生成的响应是图片
		response.setContentType("image/png");

		int width = 70, height = 20;
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		Graphics g = image.getGraphics();
		Random random = new Random();
		g.setColor(getRandColor(200, 250));
		g.fillRect(1, 1, width - 1, height - 1);

		g.setColor(getRandColor(160, 200));

		// 画随机线
		for (int i = 0; i < 20; i++) {
			int x = random.nextInt(width - 1);
			int y = random.nextInt(height - 1);
			int z = random.nextInt(5);
			if (z < 2)
				z = 2;
			g.fillRoundRect(x, y, z, z, z / 2, z / 2);
		}
		g.setColor(new Color(219, 0, 0));
		g.drawRect(0, 0, width - 1, height - 1);
		g.setFont(mFont);

		// 生成随机数,并将随机数字转换为字母
		String sRand = "";
		for (int i = 0; i < 4; i++) {
			int itmp = random.nextInt(26) + 65;
			char ctmp = (char) itmp;
			sRand += String.valueOf(ctmp);
			g.setColor(new Color(20 + random.nextInt(110), 20 + random
					.nextInt(110), 20 + random.nextInt(110)));
			g.drawString(String.valueOf(ctmp), 15 * i + 5, 16);
		}
		g.dispose();
		ImageIO.write(image, "PNG", response.getOutputStream());
		WebSession session = (WebSession) WebSession.getCurrentSession(request);
		session.getParameters().put("vercode", sRand);
		try {
			session.save();
		} catch (InterruptedException e) {
		}
	}

	public void destroy() {
	}
}
