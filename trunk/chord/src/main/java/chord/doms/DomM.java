/*
 * Copyright (c) 2008-2009, Intel Corporation.
 * Copyright (c) 2006-2007, The Trustees of Stanford University.
 * All rights reserved.
 */
package chord.doms;

import joeq.Class.jq_Class;
import joeq.Class.jq_Method;
import joeq.Class.jq_NameAndDesc;
import joeq.Class.jq_Type;
import chord.project.Chord;
import chord.project.Program;
import chord.project.ProgramDom;
import chord.project.Project;
import chord.visitors.IMethodVisitor;

/**
 * Domain of methods.
 * <p>
 * The 0th element in this domain is the main method of the program.
 * <p>
 * The 1st element in this domain is the <tt>start()</tt> method
 * of class <tt>java.lang.Thread</tt>, if this method is reachable
 * from the main method of the program.
 * <p>
 * The above two methods are the entry-point methods of the implicitly
 * created main thread and each explicitly created thread,
 * respectively.  Due to Chord's emphasis on concurrency, these
 * methods are referenced frequently by various predefined program
 * analyses expressed in Datalog, and giving them special indices
 * makes it convenient to reference them in those analyses.
 * 
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
@Chord(
	name = "M"
)
public class DomM extends ProgramDom<jq_Method>
		implements IMethodVisitor {
	public void init() {
		// Reserve index 0 for the main method of the program.
		// Reserver index 1 for the start() method of java.lang.Thread
		// if it exists.
		jq_Method mainMethod = Program.v().getMainMethod();
		getOrAdd(mainMethod);
		jq_Method startMethod = Program.v().getThreadStartMethod();
		if (startMethod != null)
			getOrAdd(startMethod);
	}
	public void visit(jq_Class c) { }
	public void visit(jq_Method m) {
		getOrAdd(m);
	}
	public String toXMLAttrsString(jq_Method m) {
		jq_Class c = m.getDeclaringClass();
		String methName = m.getName().toString();
		String sign = c.getName() + ".";
		if (methName.equals("<init>"))
			sign += "&lt;init&gt;";
		else if (methName.equals("<clinit>"))
			sign += "&lt;clinit&gt;";
		else
			sign += methName;
		sign += Program.methodDescToStr(m.getDesc().toString());
		String file = Program.getSourceFileName(c);
		int line = 0;  // TODO
		return "sign=\"" + sign +
			"\" file=\"" + file +
			"\" line=\"" + line + "\"";
	}
}
