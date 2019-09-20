// Min Min ,Max Min 

	

package org.workflowsim.examples.scheduling;	

	

	import java.io.File;

	import java.util.Calendar;

	import java.util.List;

	import org.cloudbus.cloudsim.Log;

	import org.cloudbus.cloudsim.core.CloudSim;

	import org.workflowsim.CondorVM;

	import org.workflowsim.WorkflowDatacenter;

	import org.workflowsim.Job;

	import org.workflowsim.WorkflowEngine;

	import org.workflowsim.WorkflowPlanner;

	import org.workflowsim.utils.ClusteringParameters;

	import org.workflowsim.utils.OverheadParameters;

	import org.workflowsim.utils.Parameters;

	import org.workflowsim.utils.ReplicaCatalog;

	

	// This MINMIN Scheduling Algorithm

	

	public class MINMINSchedulingAlgorithmExample extends DataAwareSchedulingAlgorithmExample {

	

	public static void main(String[] args) {

	

	try {

	

	int vmNum = 5;

	

	String daxPath = "/Users/weiweich/NetBeansProjects/WorkflowSim-1.0/config/dax/Montage_100.xml";

	

	File daxFile = new File(daxPath);

	if (!daxFile.exists()) {

	Log.printLine("Warning: Please replace daxPath with the physical path in your working environment!");

	return;

	}

	

	Parameters.SchedulingAlgorithm sch_method = Parameters.SchedulingAlgorithm.MINMIN;

	Parameters.PlanningAlgorithm pln_method = Parameters.PlanningAlgorithm.INVALID;

	ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.LOCAL;

	

	ClusteringParameters.ClusteringMethod method = ClusteringParameters.ClusteringMethod.NONE;

	ClusteringParameters cp = new ClusteringParameters(0, 0, method, null);

	

	

	Parameters.init(vmNum, daxPath, null,

	null, op, cp, sch_method, pln_method,

	null, 0);

	ReplicaCatalog.init(file_system);

	

	

	int num_user = 1;   // number of grid users

	Calendar calendar = Calendar.getInstance();

	boolean trace_flag = false;  // mean trace events

	

	

	CloudSim.init(num_user, calendar, trace_flag);

	

	WorkflowDatacenter datacenter0 = createDatacenter("Datacenter_0");

	

	

	WorkflowPlanner wfPlanner = new WorkflowPlanner("planner_0", 1);

	

	WorkflowEngine wfEngine = wfPlanner.getWorkflowEngine();

	

	List<CondorVM> vmlist0 = createVM(wfEngine.getSchedulerId(0), Parameters.getVmNum());

	

	

	wfEngine.submitVmList(vmlist0, 0);

	

	

	wfEngine.bindSchedulerDatacenter(datacenter0.getId(), 0);

	

	CloudSim.startSimulation();

	List<Job> outputList0 = wfEngine.getJobsReceivedList();

	CloudSim.stopSimulation();

	printJobList(outputList0);

	} catch (Exception e) {

	Log.printLine("The simulation has been terminated due to an unexpected error");

	}

	}

	}