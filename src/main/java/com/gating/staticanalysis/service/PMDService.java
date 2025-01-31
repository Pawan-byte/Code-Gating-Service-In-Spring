package com.gating.staticanalysis.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.gating.service.ProcessUtility;
import com.gating.toolconfig.service.PMDConfig;
import com.gating.toolconfig.service.PMDConfigService;
import com.gating.toolconfig.service.ThresholdConfigService;
import com.gating.toolconfig.service.ToolResponse;
import com.gating.utility.ThresholdComparison;

@Service
public class PMDService {

  Logger logger = LoggerFactory.getLogger(PMDService.class);


  @Autowired
  ProcessUtility processUtility;

  @Autowired
  ThresholdConfigService thresholdConfService;

  @Autowired
  PMDConfigService pmdConfigService;


  private static final String PMD_BIN_PATH = "static-code-analyzers/pmd/bin;";

  public List<String> getCommand(String srcPath, PMDConfig params) {

    final StringJoiner pmdCommand = new StringJoiner(" ");
    pmdCommand.add("pmd -d");
    pmdCommand.add(srcPath);
    pmdCommand.add("-f");
    pmdCommand.add(PMDConfig.outputFormat);
    pmdCommand.add("-R");
    pmdCommand.add(params.getRuleSet());
    pmdCommand.add(">");
    pmdCommand.add(PMDConfig.pmdReportPath);

    final List<String> command = new ArrayList<String>();
    command.add("cmd");
    command.add("/c");
    command.add(pmdCommand.toString());
    return command;
  }

  public int getNumberOfViolations(String pmdreportpath) {

    int violations = 0;

    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    Document doc = null;

    try {
      builder = factory.newDocumentBuilder();
    } catch (final ParserConfigurationException e) {
      logger.error("ParserConfigurationException occured", e);
    }

    try {
      if (builder != null) {
        doc = builder.parse(pmdreportpath);
      }
    } catch (final SAXException e) {
      logger.error("SAXException occured", e);
    } catch (final IOException e) {
      logger.error("File not found exception occured", e);
    }

    if (doc != null && doc.getElementsByTagName("file") != null) {
      final NodeList fileList = doc.getElementsByTagName("file");
      for (int i = 0; i < fileList.getLength(); i++) {
        final Node p = fileList.item(i);
        if (p.getNodeType() == Node.ELEMENT_NODE) {
          final Element file = (Element) p;
          final NodeList violationList = file.getChildNodes();
          violations += violationList.getLength();
        }
      }
    }

    return violations;
  }


  public ToolResponse<Integer> run(String srcPath) {

    final PMDConfig params = pmdConfigService.getConfig();
    processUtility.initProcessBuilder(PMD_BIN_PATH);
    processUtility.runProcess(getCommand(srcPath, params));

    final int warnings = getNumberOfViolations(PMDConfig.pmdReportPath);
    final int warningsThreshold = thresholdConfService.getThresholds().getNoOfWarnings();

    final String decision =
        ThresholdComparison.isLessThanThreshold(warnings, warningsThreshold) ? "Go" : "No Go";

    return new ToolResponse<Integer>(warnings, warningsThreshold, decision);
  }

}
