package d.cgunit;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import d.cgunit.core.CGUnitConnect;

public class CGUnitLaunchShortcut implements ILaunchShortcut {

	/**
	 * 文件树选择运行
	 */
	@Override
	public void launch(ISelection arg0, String arg1) {
		
		if(arg0 instanceof IStructuredSelection){
			Object element = ((IStructuredSelection)arg0).getFirstElement();
			
			if(element instanceof IJavaElement){
				IJavaElement javaElement = (IJavaElement)element;
//				System.out.println(javaElement.getResource().getLocationURI()); // file:/F:/runtime-EclipseApplication/Tests/src/test/Tests.java
//				System.out.println(javaElement.getResource().getLocation().makeAbsolute().toString());// F:/runtime-EclipseApplication/Tests/src/test/Tests.java
//				System.out.println(javaElement.getResource().getLocation().toFile().getAbsolutePath());// F:\runtime-EclipseApplication\Tests\src\test\Tests.java
				new CGUnitConnect().run(javaElement.getResource().getLocation().toFile().getAbsolutePath());
			}
		}
	}

	/**
	 * 编辑页面运行
	 */
	@Override
	public void launch(IEditorPart arg0, String arg1) {
		
		IEditorInput editorInput = arg0.getEditorInput();
		if(editorInput instanceof IFileEditorInput){
			IFile file = ((IFileEditorInput)editorInput).getFile();
//			System.out.println(file.getLocationURI()); // file:/F:/runtime-EclipseApplication/Tests/src/test/Tests.java
//			System.out.println(file.getLocation().makeAbsolute().toString()); // F:/runtime-EclipseApplication/Tests/src/test/Tests.java
//			System.out.println(file.getLocation().makeAbsolute().toFile().getAbsolutePath()); // F:\runtime-EclipseApplication\Tests\src\test\Tests.java
			new CGUnitConnect().run(file.getLocation().makeAbsolute().toFile().getAbsolutePath());
		}
	}
}
