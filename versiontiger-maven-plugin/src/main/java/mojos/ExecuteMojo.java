package mojos;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.inventage.tools.versiontiger.CheckFailedException;
import com.inventage.tools.versiontiger.internal.impl.FileExecution;

/**
 * Invoke the Version-Tiger plugin.<br>
 * The Version-Tiger statements file is required as parameter.<br>
 */
@Mojo( name = "execute", requiresProject = false )
public class ExecuteMojo extends AbstractMojo {
	
    /**
     * Location of the versiontiger statements file.
     */
    @Parameter( defaultValue = "", property = "statementsFile", required = true )
    private File statementsFile;

    public void execute() throws MojoFailureException, MojoExecutionException {
    	try {
    		new FileExecution().execute(statementsFile, new MavenLogger(getLog()));
    	}
    	catch (CheckFailedException e) {
    		throw new MojoFailureException(e.getMessage());
    	}
    	catch (Exception e) {
    		throw new MojoExecutionException(e.getMessage());
    	}
    }

}
