package vknue.mahjong.utilities;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import vknue.mahjong.converters.DateTimeConverter;
import vknue.mahjong.mahjong.Constants;
import vknue.mahjong.mahjong.GameMoveType;
import vknue.mahjong.models.GameMove;
import vknue.mahjong.models.PlayerType;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class XMLUtils {

    private XMLUtils() {
    }

    public static void SaveGame(List<GameMove> gameMoves) {

        try {
            Document document = createDocument("gameMoves");


            gameMoves.forEach(x -> {
                Element gameMove = document.createElement("gameMove");
                document.getDocumentElement().appendChild(gameMove);

                gameMove.appendChild(createElement(document, "player", x.getPlayer().toString()));
                gameMove.appendChild(createElement(document, "gameMoveType", x.getGameMoveType().toString()));
                gameMove.appendChild(createElement(document, "tileName", x.getTileName()));
                gameMove.appendChild(createElement(document, "time", String.valueOf(DateTimeConverter.localDateTimeToMilliseconds(x.getTime()))));
            });

            saveDocument(document, Constants.REPLAY_FILE_NAME);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    private static Document createDocument(String element) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation domImplementation = builder.getDOMImplementation();
        DocumentType documentType = domImplementation.createDocumentType("DOCTYPE", null, "gameMoves.dtd");
        return domImplementation.createDocument(null, element, documentType);
    }

    private static Node createElement(Document document, String tagName, String data) {
        Element element = document.createElement(tagName);
        Text text = document.createTextNode(data);
        element.appendChild(text);
        return element;
    }

    private static void saveDocument(Document document, String fileName) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, document.getDoctype().getSystemId());

        String filePath = Constants.REPLAY_FILE_NAME;
        File filePathAbsolute = new File(filePath);
        System.out.println(filePathAbsolute.getAbsoluteFile().toPath());
        transformer.transform(new DOMSource(document), new StreamResult(new File(Constants.REPLAY_FILE_NAME)));
    }

    public static List<GameMove> LoadGame() {
        List<GameMove> gameMoveList = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(Constants.REPLAY_FILE_NAME));
            Node node = document.getDocumentElement();

            NodeList childNodes = node.getChildNodes();

            int numberOfNodes = childNodes.getLength();

            for(int n = 0; n < numberOfNodes; n++) {

                Node parentNode = childNodes.item(n);

                if (parentNode.getNodeType() == Node.ELEMENT_NODE) {

                    NodeList gameMoveNodes = parentNode.getChildNodes();

                    PlayerType player = null;
                    GameMoveType gameMoveType = null;
                    String tileName = null;
                    LocalDateTime time = null;

                    for (int i = 0; i < gameMoveNodes.getLength(); i++) {

                        Node moveNode = gameMoveNodes.item(i);

                        if (moveNode.getNodeType() == Node.ELEMENT_NODE) {

                            switch (moveNode.getNodeType()) {
                                case Node.ELEMENT_NODE:
                                    Element nodeElement = (Element) moveNode;
                                    String nodeName = nodeElement.getNodeName();
                                    if (nodeName.equals("player")) {
                                        String nodeValue = nodeElement.getTextContent();
                                        player = PlayerType.valueOf(nodeValue);
                                    }
                                    else if(nodeName.equals("gameMoveType")) {
                                        gameMoveType = GameMoveType.valueOf(nodeElement.getTextContent());
                                    }
                                    else if(nodeName.equals("tileName")) {
                                        tileName = nodeElement.getTextContent();
                                    }
                                    else if(nodeName.equals("time")) {
                                        time = DateTimeConverter.millisecondsToLocalDateTime(Long.parseLong(nodeElement.getTextContent()));
                                    }
                                    break;
                                case Node.TEXT_NODE:
                                    break;
                                case Node.CDATA_SECTION_NODE:
                                    break;
                            }
                        }
                    }
                    GameMove gameMove = new GameMove(player,gameMoveType, tileName, time);
                    gameMoveList.add(gameMove);
                }
            }
        }
        catch(ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace();
        }
        gameMoveList.forEach(System.out::println);
        return gameMoveList;
    }

}
