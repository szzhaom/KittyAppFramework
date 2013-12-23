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
	int x, y;

	int getRight() {
		return x + image.getWidth();
	}

	int getBottom() {
		return y + image.getHeight();
	}

	int getWidth() {
		return image.getWidth();
	}

	int getHeight() {
		return image.getHeight();
	}
}

public class TexturePacker {
	public static void main(String[] args) {
		// new TexturePacker().generate(150,
		// "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/ImageSources/loading/",
		// "loading_*.png",
		// "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/loading.png",
		// "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/loading.plist");
		 new TexturePacker().generate(560,
		 "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/ImageSources/buttons/",
		 "*.png",
		 "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/buttons.png",
		 "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/buttons.plist");
//		new TexturePacker().generate(560, "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/ImageSources/heads/",
//				"*.png", "/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/heads.png",
//				"/zhaom/product/cocos2d-x-2.2/projects/ThreeKillSolts/Resources/heads.plist");
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

			System.out.println(pattern);
			Pattern regExp = Pattern.compile(pattern);
			File list[] = pathFile.listFiles();
			for (File file : list) {
				if (regExp.matcher(file.getName()).matches()) {
					ImageProperty p = new ImageProperty();
					p.image = ImageIO.read(file);
					p.file = file;
					imageList.add(p);
					if (lastImage == null) {
						p.x = 0;
						p.y = 0;
						if (p.getWidth() > maxWidth)
							maxWidth = p.getWidth();
					} else {
						if (p.getWidth() > maxWidth)
							maxWidth = p.getWidth();
						if (lastImage.getRight() + p.getWidth() > maxWidth) {
							p.x = 0;
							p.y = height;
						} else {
							p.y = lastImage.y;
							p.x = lastImage.getRight();
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
				out.getGraphics().drawImage(p.image, p.x, p.y, null);
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
					el.setTextContent("{{" + p.x + "," + p.y + "},{" + p.getWidth() + "," + p.getHeight() + "}}");
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
					el.setTextContent("{{0,0},{" + p.getWidth() + "," + p.getHeight() + "}}");
					dictChild.appendChild(el);

					el = doc.createElement("key");
					el.setTextContent("sourceSize");
					dictChild.appendChild(el);
					el = doc.createElement("string");
					el.setTextContent("{" + p.getWidth() + "," + p.getHeight() + "}");
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
