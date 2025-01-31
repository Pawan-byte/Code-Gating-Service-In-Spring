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
import com.gating.toolconfig.service.ThresholdConfigService;
import com.gating.toolconfig.service.ToolResponse;
import com.gating.toolconfig.service.VCGConfig;
import com.gating.toolconfig.service.VCGConfigService;
import com.gating.utility.ThresholdComparison;

@Service
public class VCGService {

  Logger logger = LoggerFactory.getLogger(VCGService.class);

  @Autowired
  ProcessUtility processUtility;

  @Autowired
  VCGConfigService vcgConfigService;

  @Autowired
  ThresholdConfigService thresholdConfigService;

  public List<String> getCommand(String srcPath, VCGConfig vcgConfig) {
    final StringJoiner vcgCommand = new StringJoiner(" ");
    vcgCommand.add("Visualcodegrepper.exe");
    vcgCommand.add("-c");
    vcgCommand.add("-l");
    vcgCommand.add("Java");
    vcgCommand.add("-t");
    vcgCommand.add(srcPath);
    vcgCommand.add(vcgConfig.getOutputFormat());
    vcgCommand.add(VCGConfig.VCG_REPORT_PATH);

    final List<String> command = new ArrayList<String>();
    command.add("cmd");
    command.add("/c");
    command.add(vcgCommand.toString());
    return command;
  }

  public int getSecurityIssuesCountFromReport(String vcgReportPath) {

    int securityIssuesCount = 0;
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    Document doc = null;

    try {
      builder = factory.newDocumentBuilder();
    } catch (final ParserConfigurationException e) {
      logger.error("Report file could not be parsed", e);
    }

    try {
      if (builder != null) {
        doc = builder.parse(vcgReportPath);
      }
    } catch (final SAXException e) {
      logger.error("SAXException occurred", e);
    } catch (final IOException e) {
      logger.error("IOException occurred", e);
    }


    if (doc != null && doc.getElementsByTagName("CodeIssueCollection") != null) {
      final NodeList issueCollection = doc.getElementsByTagName("CodeIssueCollection");
      for (int i = 0; i < issueCollection.getLength(); i++) {
        final Node p = issueCollection.item(i);
        if (p.getNodeType() == Node.ELEMENT_NODE) {
          final Element file = (Element) p;
          final NodeList codeIssueList = file.getChildNodes();
          securityIssuesCount += codeIssueList.getLength();
        }
      }
    }

    return securityIssuesCount;
  }

  public ToolResponse<Integer> run(String srcPath) {

    final VCGConfig vcgConfig = vcgConfigService.getConfig();

    processUtility.initProcessBuilder(VCGConfig.VCG_BIN_PATH);
    processUtility.runProcess(getCommand(srcPath, vcgConfig));
    final int securityIssues =  getSecurityIssuesCountFromReport(VCGConfig.VCG_REPORT_PATH);
    final int threshold = thresholdConfigService.getThresholds().getSecurityIssuesCount();

    final String finalDecision = ThresholdComparison.isLessThanThreshold(securityIssues, threshold) ? "Go" : "No Go";

    return new ToolResponse<Integer>(securityIssues, threshold, finalDecision);
  }



}
