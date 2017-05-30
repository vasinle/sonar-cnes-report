package fr.cnes.sonar.report;

import fr.cnes.sonar.report.exporters.JsonExporter;
import fr.cnes.sonar.report.exporters.XlsXExporter;
import fr.cnes.sonar.report.exporters.XmlExporter;
import fr.cnes.sonar.report.exporters.docx.DocXExporter;
import fr.cnes.sonar.report.factory.ReportFactory;
import fr.cnes.sonar.report.model.QualityProfile;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.params.Params;
import fr.cnes.sonar.report.params.ParamsFactory;

import java.util.logging.Logger;

/**
 * Main entry point
 * @author begarco
 */
public class ReportCommandLine {

    /**
     * Logger of this class
     */
    private static final Logger LOGGER = Logger.getLogger(ReportCommandLine.class.getName());

    /**
     * Main method
     * @param args arguments that will be preprocessed
     */
    public static void main(String[] args) {
        // main catch all exceptions
        try {
            // preparing args
            Params params = new ParamsFactory().create(args);

            // Files exporters
            DocXExporter docXExporter = new DocXExporter();
            XmlExporter profileExporter = new XmlExporter();
            JsonExporter gateExporter = new JsonExporter();
            XlsXExporter issuesExporter = new XlsXExporter();

            // Producing the report
            Report superReport = new ReportFactory(params).create();

            // Export all
            // export each linked quality profile
            for(QualityProfile qp : superReport.getQualityProfiles()) {
                profileExporter.export(qp.getConf(), params, qp.getKey());
            }
            // export the quality gate
            gateExporter.export(superReport.getQualityGate().getConf(),params,superReport.getQualityGate().getName());
            // export the full docx report
            docXExporter.export(superReport, params, "analysis-report.docx");
            // export the xlsx issues' list
            issuesExporter.export(superReport, params, "issues-report.xlsx");
        } catch (Exception e) { // on each exception
            // it logs all the stack trace
            for (StackTraceElement ste: e.getStackTrace()) {
                LOGGER.severe(ste.toString());
            }
            // prints the help
            help();
        }
    }

    /**
     * Provide help on bad command line
     */
    private static void help() {
        // only log the help
        LOGGER.info("Bienvenue dans Sonar Report CNES\n" +
                "Voici l'aide pour exécuter correctement cette commande :\n" +
                "  > --sonar.url [mandatory]\n" +
                "  > --sonar.project.id [mandatory]\n" +
                "  > --sonar.project.quality.profile\n" +
                "  > --sonar.project.quality.gate\n" +
                "  > --project.name\n" +
                "  > --report.author\n" +
                "  > --report.date\n" +
                "  > --report.path\n" +
                "  > --report.template\n" +
                "Exemple :\n" +
                "java -jar sonar-report-cnes.jar --sonar.url http://sonarqube:9000 --sonar.project.id genius-sonar");
    }

}
