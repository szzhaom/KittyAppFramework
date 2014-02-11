package kitty.kaf.tools.tp;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Cocos2dx贴图打包工具
 * 
 * @author 赵明
 * 
 */
class ImageProperty {

	BufferedImage image;
	File file;
	private int x, y;

	public int getOriginalY() {
		return y;
	}

	public int getOriginalX() {
		return x;
	}

	public int getOriginalWidth() {
		return image.getWidth();
	}

	public int getOriginalHeight() {
		return image.getHeight();
	}

	public int getX() {
		return x + 2;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y + 2;
	}

	public void setY(int y) {
		this.y = y;
	}

	int getRight() {
		return x + image.getWidth() + 4;
	}

	int getBottom() {
		return y + image.getHeight() + 4;
	}

	int getWidth() {
		return image.getWidth() + 4;
	}

	int getHeight() {
		return image.getHeight() + 4;
	}
}

public class TexturePacker {
	public static void main(String[] args) {
		// new TexturePacker().generate(150,
		// "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/ImageSources/loading/",
		// "loading_*.png",
		// "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/loading.png",
		// "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/loading.plist");
//		new TexturePacker().generate(1000, "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/ImageSources/buttons/loginpage",
//				"*.png", "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/buttons/login_buttons.png",
//				"/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/buttons/login_buttons.plist");
		new TexturePacker().generate(1000, "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/ImageSources/buttons/main",
				"*.png", "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/buttons/main_buttons.png",
				"/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/buttons/main_buttons.plist");
		new TexturePacker().generate(1000, "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/ImageSources/buttons/common",
				"*.png", "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/buttons/common_buttons.png",
				"/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/buttons/common_buttons.plist");
//		new TexturePacker().generate(1000, "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/ImageSources/other",
//				"*.png", "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/other/other_imgs.png",
//				"/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/other/other_imgs.plist");
//		new TexturePacker().generate(1000, "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/ImageSources/texts",
//				"*.png", "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/other/title_text_imgs.png",
//				"/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/other/title_text_imgs.plist");
//		new TexturePacker().generate(1000, "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/ImageSources/daylogin",
//				"*.png", "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/other/day_login_imgs.png",
//				"/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/other/day_login_imgs.plist");
		// new TexturePacker().generate(560,
		// "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/ImageSources/heads/",
		// "*.png",
		// "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/heads.png",
		// "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/heads.plist");
	}

	public void generate(int maxWidth, String path, String pattern, String outputPngFile, String outputPlistFile) {
		try {
			List<ImageProperty> imageList = new ArrayList<ImageProperty>();
			int width = 0, height = 0;
			ImageProperty lastImage = null;
			File pathFile = new File(path);
			pattern = pattern.replace('.', '#');
			pattern = pattern.replaceAll("#", "\\\\.");
			pattern = pattern.replace('*', '#');
			pattern = pattern.replaceAll("#", ".*");
			pattern = pattern.replace('?', '#');
			pattern = pattern.replaceAll("#", ".?");
			pattern = "^" + pattern + "$";

			Pattern regExp = Pattern.compile(pattern);
			File list[] = pathFile.listFiles();
			for (File file : list) {
				if (regExp.matcher(file.getName()).matches()) {
					ImageProperty p = new ImageProperty();
					p.image = ImageIO.read(file);
					p.file = file;
					imageList.add(p);
					if (lastImage == null) {
						p.setX(0);
						p.setY(0);
						if (p.getWidth() > maxWidth)
							maxWidth = p.getWidth();
					} else {
						if (p.getWidth() > maxWidth)
							maxWidth = p.getWidth();
						if (lastImage.getRight() + p.getWidth() > maxWidth) {
							p.setX(0);
							p.setY(height);
						} else {
							p.setY(lastImage.getOriginalY());
							p.setX(lastImage.getRight());
						}
					}
					if (height < p.getBottom())
						height = p.getBottom();
					if (width < p.getRight())
						width = p.getRight();
					lastImage = p;
				}
			}
			BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
			out.createGraphics().getDeviceConfiguration().createCompatibleImage(width, height);
			for (ImageProperty p : imageList)
				out.getGraphics().drawImage(p.image, p.getX(), p.getY(), null);
			File pngFile = new File(outputPngFile);
			ImageIO.write(out, "png", pngFile);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			try {
				builder = factory.newDocumentBuilder();
				Document doc = builder.newDocument();
				Element root = doc.createElement("plist");
				doc.appendChild(root);
				Element rootDict = doc.createElement("dict");
				root.appendChild(rootDict);
				Element key = doc.createElement("key");
				rootDict.appendChild(key);
				key.setTextContent("frames");
				Element dict = doc.createElement("dict");
				rootDict.appendChild(dict);
				for (ImageProperty p : imageList) {
					Element keyChild = doc.createElement("key");
					dict.appendChild(keyChild);
					keyChild.setTextContent(p.file.getName());
					Element dictChild = doc.createElement("dict");
					dict.appendChild(dictChild);
					Element el = doc.createElement("key");
					el.setTextContent("frame");
					dictChild.appendChild(el);
					el = doc.createElement("string");
					el.setTextContent("{{" + p.getX() + "," + p.getY() + "},{" + p.getOriginalWidth() + "," + p.getOriginalHeight()
							+ "}}");
					dictChild.appendChild(el);

					el = doc.createElement("key");
					el.setTextContent("offset");
					dictChild.appendChild(el);
					el = doc.createElement("string");
					el.setTextContent("{0,0}");
					dictChild.appendChild(el);

					el = doc.createElement("key");
					el.setTextContent("rotated");
					dictChild.appendChild(el);
					el = doc.createElement("false");
					dictChild.appendChild(el);

					el = doc.createElement("key");
					el.setTextContent("sourceColorRect");
					dictChild.appendChild(el);
					el = doc.createElement("string");
					el.setTextContent("{{0,0},{" + p.getOriginalWidth() + "," + p.getOriginalHeight() + "}}");
					dictChild.appendChild(el);

					el = doc.createElement("key");
					el.setTextContent("sourceSize");
					dictChild.appendChild(el);
					el = doc.createElement("string");
					el.setTextContent("{" + p.getOriginalWidth() + "," + p.getOriginalHeight() + "}");
					dictChild.appendChild(el);
				}
				Element keyChild = doc.createElement("key");
				rootDict.appendChild(keyChild);
				keyChild.setTextContent("metadata");
				Element dictChild = doc.createElement("dict");
				rootDict.appendChild(dictChild);
				Element el = doc.createElement("key");
				dictChild.appendChild(el);
				el.setTextContent("format");
				el = doc.createElement("integer");
				dictChild.appendChild(el);
				el.setTextContent("2");

				el = doc.createElement("key");
				dictChild.appendChild(el);
				el.setTextContent("realTextureFileName");
				el = doc.createElement("string");
				dictChild.appendChild(el);
				el.setTextContent(pngFile.getName());

				el = doc.createElement("key");
				dictChild.appendChild(el);
				el.setTextContent("size");
				el = doc.createElement("string");
				dictChild.appendChild(el);
				el.setTextContent("{" + width + "," + height + "}");

				el = doc.createElement("key");
				dictChild.appendChild(el);
				el.setTextContent("textureFileName");
				el = doc.createElement("string");
				dictChild.appendChild(el);
				el.setTextContent(pngFile.getName());

				TransformerFactory tfactory = TransformerFactory.newInstance();
				Transformer transformer = tfactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				FileOutputStream os = new FileOutputStream(new File(outputPlistFile));
				StreamResult sr = new StreamResult(os);
				transformer.setOutputProperty("encoding", "utf-8");
				transformer.transform(source, sr);
			} catch (ParserConfigurationException e) {
			} catch (TransformerConfigurationException e) {
			} catch (TransformerException e) {
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
