package mojos;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.inventage.tools.versiontiger.internal.impl.Main;

@Mojo( name = "execute")
public class ExecuteMojo extends AbstractMojo {
	
    /**
     * Location of the versiontiger file.
     */
    @Parameter( defaultValue = "", property = "versiontigerFile", required = true )
    private File versiontigerFile;

    public void execute() throws MojoExecutionException {
    	String[] args = {versiontigerFile.toString()};
    	new Main().executeCommands(args);
    }    
}
