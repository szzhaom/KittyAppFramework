package kitty.kaf.trade.web;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import kitty.kaf.exceptions.CoreException;
import kitty.kaf.logging.KafLogger;
import kitty.kaf.logging.RequestLoggerDataSource;
import kitty.kaf.trade.pack.HttpRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

abstract public class XMLExecutor implements WebExecutor {
	private final static KafLogger logger = KafLogger
			.getLogger(XMLExecutor.class);

	@Override
	public void execute(HttpRequest request, HttpServletResponse response)
			throws Throwable {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			if (logger.isDebugEnabled()) {
				logger.debug(new RequestLoggerDataSource(request.getRequest()));
			}
			builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElement("response");
			doc.appendChild(root);
			Element result = doc.createElement("result");
			root.appendChild(result);
			Element content = doc.createElement("content");
			root.appendChild(content);
			try {
				Document ndoc = doExecute(request, response, doc, content,
						result);

				if (ndoc != null && ndoc != doc) {
					doc = ndoc;
					NodeList ls = doc.getElementsByTagName("result");
					if (ls.getLength() > 0) {
						result = (Element) ls.item(0);
					}
				}
				result.setAttribute("success", "true");
				result.setAttribute("message", "操作成功");
			} catch (CoreException e) {
				if (e.getData() != null && e.getData() instanceof Document) {
					doc = (Document) e.getData();
					NodeList ls = doc.getElementsByTagName("result");
					if (ls.getLength() > 0) {
						result = (Element) ls.item(0);
					}
				}
				result.setAttribute("success", "false");
				result.setAttribute("message", e.getMessage());
			} catch (Throwable e) {
				if ((e.getCause() == null && !(e instanceof CoreException))
						|| (e.getCause() != null && !(e.getCause() instanceof CoreException))) {
					logger.error("操作失败：", e);
				}
				result.setAttribute("success", "false");
				result.setAttribute("message", e.getMessage());
			}

			response.setCharacterEncoding("utf-8");
			TransformerFactory tfactory = TransformerFactory.newInstance();
			Transformer transformer = tfactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult sr = new StreamResult(response.getOutputStream());
			transformer.setOutputProperty("encoding", "utf-8");
			transformer.transform(source, sr);
		} catch (ParserConfigurationException e) {
		} catch (TransformerConfigurationException e) {
		} catch (TransformerException e) {
		}
	}

	abstract protected Document doExecute(HttpRequest request,
			HttpServletResponse response, Document doc, Element content,
			Element result);

}
