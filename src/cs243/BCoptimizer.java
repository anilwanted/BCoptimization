package cs243;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.EquivalentValue;
import soot.Local;
import soot.MethodOrMethodContext;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SideEffectTester;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AddExpr;
import soot.jimple.AndExpr;
import soot.jimple.ArithmeticConstant;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.BreakpointStmt;
import soot.jimple.ConditionExpr;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.DivExpr;
import soot.jimple.DoubleConstant;
import soot.jimple.EqExpr;
import soot.jimple.Expr;
import soot.jimple.FloatConstant;
import soot.jimple.GeExpr;
import soot.jimple.GotoStmt;
import soot.jimple.GtExpr;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.LeExpr;
import soot.jimple.LongConstant;
import soot.jimple.LtExpr;
import soot.jimple.MulExpr;
import soot.jimple.NaiveSideEffectTester;
import soot.jimple.NeExpr;
import soot.jimple.NumericConstant;
import soot.jimple.OrExpr;
import soot.jimple.RemExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.SubExpr;
import soot.jimple.XorExpr;
import soot.jimple.internal.JArrayRef;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;
import soot.jimple.toolkits.scalar.AvailableExpressions;
import soot.jimple.toolkits.scalar.Evaluator;
import soot.jimple.toolkits.scalar.FastAvailableExpressions;
import soot.jimple.toolkits.scalar.SlowAvailableExpressions;
import soot.toolkits.graph.CompleteUnitGraph;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.UnitValueBoxPair;
import soot.util.Chain;

public class BCoptimizer {

    public static void main(String args[]) {
    	final String sampleClass = "JGFAssignBench";
        List<String> argsList = new ArrayList<String>(Arrays.asList(args));
        argsList.addAll(Arrays.asList(new String[] {
        		"-p", "jb", "use-original-names:true",
        		"-p", "jb.ule", "enabled:false",
        		"-p", "jb.cp-ule", "enabled:false",
        		"-p", "jb.cp", "enabled:false",
        		"-p", "jb.ulp", "enabled:false",
        		"-p", "jb.lns", "enabled:false",
        		"-p", "jb.lp", "enabled:false",
        		"-p", "jb.tr", "enabled:true",
        		"-p", "jb.dae", "enabled:false",
        		"-p", "bb", "enabled:false",
        		"-p", "jop.ule", "enabled:false",
        		"-p", "jtp", "enabled:false",
        		"-p", "wjop", "enabled:false",
        		"-p", "jj.cp-ule", "enabled:false",
        		"-p", "jj.ule", "enabled:false",
        		"-p", "gb.ule", "enabled:false",
        		// "-v",
        		//"-p", "jb.a", "enabled:false",
        		"-p", "jb.uce", "enabled:false",
        		"-p", "jb.ne", "enabled:false",
        		"-p", "tag.ln", "enabled:false",
        		//"-p", "jb.tr", "enabled:false",
        		//"-p", "jb.ls", "enabled:false",
        		//"-p", "wjtp", "enabled:false",
        		"-w",
        		"-x","java.lang.NoClassDefFoundError,java.lang.LinkageError,java.lang.String,java.lang.Error,java.lang.Throwable,java.lang.Object,java.io.Serializable,java.lang.Comparable,java.lang.CharSequence,java.lang.Long,java.lang.Float,java.lang.System,java.lang.Double,java.io.ObjectStreamField,java.util.Locale,java.lang.Deprecated,java.nio.charset.Charset,java.lang.AbstractStringBuilder,java.lang.StringCoding,java.lang.StringIndexOutOfBoundsException,java.lang.IndexOutOfBoundsException,java.util.List,sun.misc.Hashing,java.lang.Integer,java.util.Comparator,java.util.Arrays,java.io.UnsupportedEncodingException,java.lang.Math,java.lang.IllegalArgumentException,java.lang.StringBuilder,java.lang.String$1,java.util.regex.Pattern,java.lang.Character,java.util.Formatter,java.util.ArrayList,java.lang.StringBuffer,java.util.regex.Matcher,java.lang.NullPointerException,java.lang.ConditionalSpecialCasing,java.lang.String$CaseInsensitiveComparator,java.lang.StackTraceElement,java.lang.Throwable$WrappedPrintWriter,java.io.ObjectOutputStream,java.io.PrintWriter,java.lang.AssertionError,java.lang.Class,java.lang.IllegalStateException,java.util.Iterator,java.lang.Throwable$1,java.util.Map,java.lang.Thread,java.util.Collections,java.io.PrintStream,java.io.IOException,java.util.Objects,java.util.Set,java.lang.Throwable$SentinelHolder,java.io.ObjectInputStream,java.util.IdentityHashMap,java.lang.Throwable$PrintStreamOrWriter,java.lang.Throwable$WrappedPrintStream,java.lang.ClassNotFoundException,java.lang.CloneNotSupportedException,java.lang.InterruptedException,java.lang.Number,java.lang.Long$LongCache,java.lang.NumberFormatException,sun.misc.FpUtils,sun.misc.FloatingDecimal,java.lang.Cloneable,java.lang.annotation.Annotation,java.lang.Appendable,java.lang.RuntimeException,java.util.Collection,java.lang.Integer$IntegerCache,java.lang.Character$UnicodeScript,java.lang.Character$UnicodeBlock,java.lang.Character$CharacterCache,java.lang.CharacterData,java.lang.CharacterName,java.lang.Character$Subset,java.io.Closeable,java.io.Flushable,java.util.AbstractList,java.util.RandomAccess,java.lang.Boolean,java.io.ObjectInputStream$GetField,java.io.ObjectOutputStream$PutField,java.util.regex.MatchResult,java.io.OutputStream,java.io.ObjectOutput,java.io.ObjectStreamConstants,java.io.Writer,java.lang.reflect.GenericDeclaration,java.lang.reflect.Type,java.lang.reflect.AnnotatedElement,java.io.InputStream,java.lang.reflect.Array,sun.reflect.CallerSensitive,java.security.CodeSource,java.lang.NoSuchFieldException,java.util.Map$Entry,java.security.Permissions,java.security.Permission,sun.reflect.Reflection,sun.misc.Unsafe,sun.reflect.generics.scope.ClassScope,java.security.PermissionCollection,java.lang.reflect.Constructor,sun.reflect.annotation.AnnotationParser,java.security.AccessController,java.lang.reflect.GenericArrayType,java.util.HashMap,java.lang.ref.SoftReference,sun.reflect.annotation.AnnotationType,sun.security.util.SecurityConstants,sun.reflect.misc.ReflectUtil,java.lang.reflect.InvocationTargetException,sun.reflect.generics.scope.Scope,java.lang.reflect.Modifier,java.lang.NoSuchMethodException,java.lang.ClassValue,java.lang.Class$EnclosingMethodInfo,java.lang.reflect.Field,java.lang.reflect.Method,java.lang.RuntimePermission,java.lang.SecurityException,java.lang.Class$2,java.lang.Class$3,java.lang.reflect.TypeVariable,java.lang.Class$1,java.lang.ClassCastException,java.lang.Class$4,java.lang.Class$MethodArray,java.lang.Package,sun.reflect.generics.repository.MethodRepository,sun.reflect.ReflectionFactory,java.security.PrivilegedAction,sun.reflect.generics.repository.ConstructorRepository,java.lang.InstantiationException,java.lang.SecurityManager,sun.reflect.generics.factory.GenericsFactory,java.lang.InternalError,java.lang.IllegalAccessException,java.lang.ClassValue$ClassValueMap,sun.reflect.ConstantPool,sun.reflect.generics.repository.ClassRepository,sun.reflect.generics.factory.CoreReflectionFactory,sun.reflect.ReflectionFactory$GetReflectionFactoryAction,java.lang.ClassLoader,java.lang.reflect.Proxy,java.security.AllPermission,java.net.URL,java.security.ProtectionDomain,java.util.HashSet,java.lang.Enum,java.lang.Runnable,java.lang.ThreadLocal$ThreadLocalMap,java.lang.Thread$1,java.lang.Thread$UncaughtExceptionHandler,java.util.concurrent.ConcurrentMap,java.lang.Thread$Caches,java.lang.ref.ReferenceQueue,java.lang.IllegalThreadStateException,java.lang.ThreadLocal,java.lang.ref.Reference,java.lang.NoSuchMethodError,java.lang.Thread$WeakClassKey,sun.nio.ch.Interruptible,java.lang.Thread$State,java.lang.ThreadDeath,sun.misc.VM,java.security.AccessControlContext,java.lang.Exception,java.lang.ThreadGroup,java.io.FilterOutputStream,java.io.ObjectInput,java.util.AbstractMap,java.lang.ReflectiveOperationException,java.lang.Iterable,java.lang.AutoCloseable,java.util.AbstractCollection,java.io.DataOutput,java.security.Guard,sun.reflect.generics.scope.AbstractScope,java.lang.reflect.AccessibleObject,java.lang.reflect.Member,java.security.BasicPermission,sun.reflect.generics.repository.GenericDeclRepository,java.lang.VirtualMachineError,java.util.WeakHashMap,java.util.AbstractSet,java.lang.IncompatibleClassChangeError,java.lang.ref.WeakReference,java.io.DataInput,sun.reflect.generics.repository.AbstractRepository,java.io.Console,java.util.Properties,java.nio.channels.Channel,java.util.Locale$Cache,sun.util.locale.BaseLocale,sun.util.locale.LocaleExtensions,java.util.Locale$Category,java.util.MissingResourceException,sun.util.resources.OpenListResourceBundle,java.text.MessageFormat,java.io.ObjectStreamException,java.util.Locale$1,java.nio.charset.spi.CharsetProvider,java.util.SortedMap,java.nio.charset.CharsetDecoder,java.nio.charset.CharsetEncoder,java.nio.CharBuffer,java.nio.ByteBuffer,java.util.ListIterator,java.util.Random,java.util.regex.Pattern$Node,java.util.regex.Pattern$GroupHead,java.util.regex.PatternSyntaxException,java.util.regex.Pattern$CharProperty,java.util.regex.Pattern$BitClass,java.io.File,java.io.FileNotFoundException,java.util.Formatter$FormatString,java.lang.ConditionalSpecialCasing$Entry,java.util.Hashtable,java.io.ObjectOutputStream$BlockDataOutputStream,java.io.ObjectOutputStream$HandleTable,java.io.ObjectOutputStream$ReplaceTable,java.io.SerialCallbackContext,java.io.ObjectOutputStream$PutFieldImpl,java.io.ObjectOutputStream$DebugTraceInfoStack,java.io.ObjectStreamClass,java.io.Externalizable,java.util.SortedSet,java.util.Enumeration,java.util.Queue,java.util.Deque,java.io.BufferedWriter,java.io.OutputStreamWriter,java.io.ObjectInputStream$BlockDataInputStream,java.io.ObjectInputStream$ValidationList,java.io.ObjectInputStream$HandleTable,java.io.ObjectInputValidation,java.io.NotActiveException,java.io.InvalidObjectException,java.io.StreamCorruptedException,sun.misc.FDBigInt,java.io.SerializablePermission,java.lang.NegativeArraySizeException,java.lang.ArrayIndexOutOfBoundsException,java.security.CodeSigner,java.security.cert.Certificate,java.net.SocketPermission,java.security.cert.CertificateFactory,sun.reflect.ConstructorAccessor,sun.reflect.annotation.ExceptionProxy,java.security.PrivilegedExceptionAction,java.security.PrivilegedActionException,java.security.DomainCombiner,java.security.AccessControlException,java.util.HashMap$Entry,java.lang.annotation.RetentionPolicy,java.net.NetPermission,java.security.SecurityPermission,java.lang.ClassValue$Entry,java.util.concurrent.atomic.AtomicInteger,java.lang.ClassValue$Identity,java.lang.ClassValue$Version,sun.reflect.generics.repository.FieldRepository,sun.reflect.FieldAccessor,sun.reflect.MethodAccessor,java.lang.Void,java.util.jar.Manifest,java.lang.Package$1,sun.reflect.LangReflectAccess,sun.reflect.generics.tree.MethodTypeSignature,sun.reflect.generics.tree.Tree,java.io.FileDescriptor,java.net.InetAddress,sun.reflect.generics.tree.FieldTypeSignature,java.lang.reflect.ParameterizedType,java.lang.reflect.WildcardType,sun.reflect.generics.tree.ClassSignature,java.util.concurrent.ConcurrentHashMap,java.util.Vector,java.util.Stack,java.lang.ClassFormatError,sun.misc.URLClassPath,java.lang.AssertionStatusDirectives,java.lang.reflect.InvocationHandler,java.net.URLStreamHandler,java.net.URLStreamHandlerFactory,java.net.MalformedURLException,java.net.URI,java.net.URISyntaxException,java.net.URLConnection,java.net.Proxy,java.security.Principal,java.security.ProtectionDomain$Key,sun.security.util.Debug,java.lang.ThreadLocal$ThreadLocalMap$Entry,java.lang.ThreadLocal$1,java.lang.ref.ReferenceQueue$Lock,java.lang.ref.Reference$Lock,sun.misc.VMNotification,java.util.WeakHashMap$Entry,sun.reflect.generics.visitor.Reifier,java.io.Reader,java.io.Console$1,java.util.Properties$LineReader,java.util.InvalidPropertiesFormatException,sun.util.locale.LocaleObjectCache,java.util.Locale$LocaleKey,sun.util.locale.BaseLocale$Cache,sun.util.locale.BaseLocale$1,sun.util.locale.Extension,java.util.ResourceBundle,java.text.Format,java.text.FieldPosition,java.text.AttributedCharacterIterator,java.text.ParsePosition,java.text.ParseException,java.text.CharacterIterator,java.nio.charset.CodingErrorAction,java.nio.charset.CoderResult,java.nio.charset.CharacterCodingException,java.nio.Buffer,java.lang.Readable,java.nio.ByteOrder,java.nio.ShortBuffer,java.nio.IntBuffer,java.nio.LongBuffer,java.nio.FloatBuffer,java.nio.DoubleBuffer,java.util.concurrent.atomic.AtomicLong,java.util.regex.Pattern$TreeInfo,java.util.regex.Pattern$1,java.util.regex.Pattern$BmpCharProperty,java.io.FileSystem,java.io.File$PathStatus,java.nio.file.Path,java.io.FilenameFilter,java.io.FileFilter,java.util.Dictionary,java.util.Hashtable$Entry,java.io.DataOutputStream,java.io.ObjectStreamClass$ExceptionInfo,java.io.ObjectStreamClass$FieldReflector,java.io.ObjectStreamClass$ClassDataSlot,java.io.InvalidClassException,java.lang.UnsupportedOperationException,sun.nio.cs.StreamEncoder,java.io.ObjectInputStream$PeekInputStream,java.io.DataInputStream,java.io.ObjectInputStream$ValidationList$Callback,java.io.ObjectInputStream$HandleTable$HandleList,java.security.cert.CertPath,java.security.Timestamp,java.security.cert.CertificateEncodingException,java.security.PublicKey,java.security.cert.CertificateException,java.security.NoSuchAlgorithmException,java.security.InvalidKeyException,java.security.NoSuchProviderException,java.security.SignatureException,java.net.UnknownHostException,java.security.Provider,java.security.cert.CertificateFactorySpi,java.security.cert.CRL,java.security.cert.CRLException,sun.reflect.generics.tree.TypeSignature,java.util.jar.Attributes,sun.reflect.generics.tree.Signature,sun.reflect.generics.tree.FormalTypeParameter,sun.reflect.generics.tree.ReturnType,sun.reflect.generics.visitor.Visitor,java.io.SyncFailedException,java.net.InetAddress$InetAddressHolder,java.net.InetAddress$Cache,java.net.InetAddressImpl,java.net.NetworkInterface,sun.net.spi.nameservice.NameService,sun.reflect.generics.tree.BaseType,sun.reflect.generics.tree.TypeArgument,sun.reflect.generics.tree.ClassTypeSignature,java.util.concurrent.ConcurrentHashMap$Segment,java.util.concurrent.ConcurrentHashMap$HashEntry,sun.misc.Resource,sun.misc.URLClassPath$Loader,sun.net.www.MessageHeader,java.net.FileNameMap,java.net.ContentHandlerFactory,java.net.ContentHandler,java.net.UnknownServiceException,java.net.Proxy$Type,java.net.SocketAddress,java.math.BigInteger,java.lang.ref.ReferenceQueue$1,java.lang.ref.Reference$1,sun.reflect.generics.visitor.TypeTreeVisitor,sun.reflect.generics.tree.ArrayTypeSignature,sun.reflect.generics.tree.TypeVariableSignature,sun.reflect.generics.tree.Wildcard,sun.reflect.generics.tree.SimpleClassTypeSignature,sun.reflect.generics.tree.BottomSignature,sun.reflect.generics.tree.ByteSignature,sun.reflect.generics.tree.BooleanSignature,sun.reflect.generics.tree.ShortSignature,sun.reflect.generics.tree.CharSignature,sun.reflect.generics.tree.IntSignature,sun.reflect.generics.tree.LongSignature,sun.reflect.generics.tree.FloatSignature,sun.reflect.generics.tree.DoubleSignature,sun.reflect.generics.tree.VoidDescriptor,java.io.NotSerializableException,sun.util.locale.BaseLocale$Key,java.util.ResourceBundle$CacheKey,java.util.ResourceBundle$Control,java.text.AttributedCharacterIterator$Attribute,java.text.Format$Field,java.text.Format$FieldDelegate,java.nio.charset.CoderResult$Cache,java.nio.charset.CoderResult$1,java.nio.file.Watchable,java.nio.file.FileSystem,java.nio.file.LinkOption,java.nio.file.WatchKey,java.nio.file.WatchService,java.nio.file.WatchEvent$Kind,java.nio.file.WatchEvent$Modifier,java.nio.channels.WritableByteChannel,java.io.FilterInputStream,java.util.Date,java.security.Key,java.security.GeneralSecurityException,java.security.KeyException,java.security.Provider$ServiceKey,java.security.Provider$Service,java.util.jar.Attributes$Name,java.util.jar.Manifest$FastInputStream,sun.reflect.generics.tree.TypeTree,java.util.LinkedHashMap,java.net.InetAddress$Cache$Type,java.net.InetAddress$CacheEntry,java.net.InterfaceAddress,java.net.SocketException,java.util.concurrent.locks.ReentrantLock,java.util.jar.JarFile,java.util.ResourceBundle$LoaderReference,java.util.ResourceBundle$Control$CandidateListCache,java.nio.file.spi.FileSystemProvider,java.nio.file.PathMatcher,java.nio.file.attribute.UserPrincipalLookupService,java.nio.file.OpenOption,java.nio.file.CopyOption,java.util.concurrent.TimeUnit,sun.util.calendar.BaseCalendar,sun.util.calendar.BaseCalendar$Date,java.security.Provider$1,java.util.LinkedHashMap$Entry,java.net.Inet4Address,java.util.concurrent.locks.Lock,java.util.concurrent.locks.ReentrantLock$Sync,java.util.concurrent.locks.Condition,java.util.zip.ZipFile,java.util.jar.JarEntry,java.util.jar.JarVerifier,java.util.zip.ZipEntry,java.util.ResourceBundle$CacheKeyReference,java.util.ResourceBundle$1,java.nio.channels.FileChannel,java.nio.file.attribute.FileAttribute,java.nio.channels.AsynchronousFileChannel,java.util.concurrent.ExecutorService,java.nio.channels.SeekableByteChannel,java.nio.file.DirectoryStream,java.nio.file.DirectoryStream$Filter,java.nio.file.FileStore,java.nio.file.AccessMode,java.nio.file.attribute.FileAttributeView,java.nio.file.attribute.BasicFileAttributes,java.nio.file.attribute.UserPrincipal,java.nio.file.attribute.GroupPrincipal,java.util.concurrent.TimeUnit$1,sun.util.calendar.AbstractCalendar,sun.util.calendar.CalendarDate,java.util.TimeZone,java.util.concurrent.locks.AbstractQueuedSynchronizer,java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject,java.util.zip.ZipConstants,java.util.zip.ZipCoder,java.util.zip.ZipException,java.util.zip.Inflater,java.io.ByteArrayOutputStream,sun.security.util.ManifestDigester,sun.security.util.ManifestEntryVerifier,java.nio.channels.spi.AbstractInterruptibleChannel,java.nio.channels.GatheringByteChannel,java.nio.channels.ScatteringByteChannel,java.nio.channels.ReadableByteChannel,java.nio.MappedByteBuffer,java.nio.channels.FileChannel$MapMode,java.nio.channels.FileLock,java.nio.channels.AsynchronousChannel,java.nio.channels.CompletionHandler,java.util.concurrent.Future,java.util.concurrent.Executor,java.util.concurrent.Callable,java.util.concurrent.ExecutionException,java.util.concurrent.TimeoutException,java.nio.channels.ByteChannel,java.nio.file.attribute.FileStoreAttributeView,java.nio.file.attribute.AttributeView,java.nio.file.attribute.FileTime,sun.util.calendar.CalendarSystem,sun.util.calendar.Era,java.util.concurrent.locks.AbstractOwnableSynchronizer,java.util.concurrent.locks.AbstractQueuedSynchronizer$Node,java.util.zip.ZStreamRef,java.util.zip.DataFormatException,sun.security.util.ManifestDigester$Position,sun.security.util.ManifestDigester$Entry,java.security.MessageDigest,sun.misc.BASE64Decoder,java.util.jar.JarException,java.nio.channels.InterruptibleChannel,java.nio.channels.AsynchronousCloseException,java.nio.file.attribute.FileTime$DaysAndNanos,sun.util.calendar.Gregorian,java.security.MessageDigestSpi,java.security.DigestException,sun.misc.CharacterDecoder,java.io.PushbackInputStream,java.nio.channels.ClosedChannelException,sun.util.calendar.Gregorian$Date,java.lang.Short,java.lang.Short$ShortCache,java.lang.Byte,java.lang.Byte$ByteCache,java.lang.InstantiationError,java.lang.ref.Finalizer,java.lang.ref.FinalReference,java.lang.ref.Finalizer$1,java.lang.ref.Finalizer$2,java.lang.ref.Finalizer$3,java.lang.ref.Finalizer$FinalizerThread,java.lang.NoSuchFieldError,java.lang.StackOverflowError,java.lang.IllegalAccessError,java.lang.ArrayStoreException,java.lang.ExceptionInInitializerError,java.lang.VerifyError,java.lang.ArithmeticException,java.lang.AbstractMethodError,java.lang.UnknownError,java.lang.ClassCircularityError,java.lang.UnsatisfiedLinkError,java.lang.OutOfMemoryError,java.lang.IllegalMonitorStateException",
        		"-no-bodies-for-excluded", "-f", "c",
        		"-main-class", sampleClass,
        		sampleClass })); // main-class
        
        args = argsList.toArray(new String[0]);
        try {
            PackManager.v().getPack("wjtp").add(new Transform("wjtp.cs243transform", new SceneTransformer() {
            	public int counter = 0;
                private Map<Value, Value> usedLocalVarMap = new HashMap<Value, Value>();
                private int round = 0;
                
                /* boolean option for each optimization. Set to false to turn off that optimization only */
                /* 1 = true, other value = false */
                int removeUn = 1;
                int removecommonSub = 1;
                int constantProp = 1;
                int copyProp = 1;
                int deadAssign = 1;
                int constantFold = 1;
                int loopInvariant = 0;
                int conditionBranch = 0;
                int unreachbleCode = 0;
                int valueRange = 0;
                
                boolean optimizationApplied = false;
                
                @Override
                protected void internalTransform(String phaseName, Map options) {
                	System.out.println("\n[CS243 Project] Optimization Start ================================");
                    
                	do {	
                		round++;
                		System.out.println("[CS243 Project] " + round + " ROUND! ****************************");
                		optimizationApplied = false;
	                	CHATransformer.v().transform();
	                    SootClass sc = Scene.v().getSootClass(sampleClass);
	                    sc.setResolvingLevel(soot.SootClass.BODIES);
	                    
	                    CallGraph callGraph = Scene.v().getCallGraph();
	
	                    SootMethod mainMethod = Scene.v().getMainClass().getMethodByName("main");
	   
	                    // Don't use following method. Implement your own code in the above methods
	                    // copyPropagationUtil(mainMethod);
	
	                    /*      1. uninitialized local elimination
								2. common subexpression elimination
								3. constant propagation 
								4. copy propagation
								5. dead assignment removal
								6. constant folding
								7. loop invariant optimization
								8. conditional branch folding
								9. unreachable code elimination
					    */
	                    
	                    //System.out.println("\n[CS243 Project] Applying optimization for main method - start");
	 
	                    SootMethod sourceMethod = mainMethod;
	                    processOptimization(sourceMethod, phaseName, options);
	 
	                    Iterator<MethodOrMethodContext> targets = new Targets(callGraph.edgesOutOf(sourceMethod));
	                    List<SootMethod> methodList = new ArrayList<SootMethod>();
	                    
	                    boolean loopMethod = true;
	                    int i = 0;
	
	                    while (loopMethod) {
	                    	while (targets.hasNext()) {
	                            SootMethod targetMethod = (SootMethod) targets.next();
	                            if(targetMethod.toString().startsWith("<java"))
	                                continue;
	                            if (!methodList.contains(targetMethod))
	                            	methodList.add(targetMethod);
	                            System.out.println("\n[" + i + "] " + sourceMethod + " method may call " + targetMethod);
	
	                            processOptimization(targetMethod, phaseName, options);
	                    	}
	
	                    	if (i < methodList.size()) {
	                    		targets = new Targets(callGraph.edgesOutOf(methodList.get(i)));
	                    		sourceMethod = methodList.get(i);
	                    		i++;
	                        }
	                    	else
	                    		loopMethod = false;
	                    }
                	} while(false);
                }
           
                
                // 1. uninitialized local elimination
                public void removeUninitiailizedLocal(SootMethod method) {
                	
                	usedLocalVarMap.clear();
                	
                	// Get body
                	Body b = method.getActiveBody();

                    /* Step1: Collect variable information - initialized, or used */
                    Chain<Unit> units = b.getUnits();
                    
                    System.out.println("\n[Uninitialized Local Variable Elimination] Start - targetMethod : " + method);
                    Iterator<Unit> stmtIt = units.snapshotIterator();
                    while (stmtIt.hasNext()) {
                        Stmt s = (Stmt) stmtIt.next();
                        
                        // Get iterator for DefBox - the left side of statement	
                        Iterator<ValueBox> leftSideIt = s.getDefBoxes().iterator();
                        
                		// Put initialized local variables into the map 
                        while (leftSideIt.hasNext()) {
                        	ValueBox defBox = (ValueBox) leftSideIt.next();
                        	if (defBox.getValue() instanceof JArrayRef) {
                        		JArrayRef test = (JArrayRef)defBox.getValue();
                        		usedLocalVarMap.put(test.getBase(),test.getBase());
                        	}
                        	if (defBox.getValue() instanceof Local) {
                        		usedLocalVarMap.put((Local)defBox.getValue(),(Local)defBox.getValue());
                        		// System.out.println("\n[Uninitialized Local Variable Elimination] DefBox : " + defBox.getValue() + " " + s);
                        	}
                        }

                        
                        // Get iterator for UseBox - the right side of statement
                        Iterator<ValueBox> rightSideIt = s.getUseBoxes().iterator();
                        while (rightSideIt.hasNext()) {
                        	// Get rightside value. For example, it can be y, z, or y+z for "x = y + z"
                        	ValueBox useBox = (ValueBox) rightSideIt.next();
                        	
                        	// If rightside of value if local variable
                        	if (useBox.getValue() instanceof Local) {

                            	if (useBox.getValue() instanceof JArrayRef) {
                            		JArrayRef test = (JArrayRef)useBox.getValue();
                            		usedLocalVarMap.put(test.getBase(),test.getBase());
                            	}
                            	
                        		Local tmpLocal = (Local) useBox.getValue();
                        		
                        		// Put used local variables into the map 
                        		usedLocalVarMap.put(tmpLocal,tmpLocal);
                       		}
                       	}
                    }

                    /* Step 2 : Remove local variables which are never initialized or used */
                    Iterator<Local> stmtIt3 = b.getLocals().snapshotIterator();
                    while (stmtIt3.hasNext()) {
                    	Local tmpLocal = stmtIt3.next();
		                	if (!tmpLocal.toString().startsWith("$") && usedLocalVarMap.get(tmpLocal) != tmpLocal) {
		                    	System.out.println("[Uninitialized Local Variable Elimination] Removing local variable : " + tmpLocal);
		                    	stmtIt3.remove();
		                    }
                    	
                    }
                    System.out.println("[Uninitialized Local Variable Elimination] End - targetMethod : " + method);
                    
                }
                
                
                
                // 2. common subexpression elimination
                public void commonSubexpressionElimination(SootMethod method) {
                    System.out.println("\n[Common Subexpression Elimination] Start - targetMethod : " + method);
                    
                    Body b = method.getActiveBody();
                    
                    /** Step1: Use soot API to get available expression **/
                    SideEffectTester sideEffect = new NaiveSideEffectTester();;
                    sideEffect.newMethod(b.getMethod());
                    
                    AvailableExpressions ae = new SlowAvailableExpressions(b);//new FastAvailableExpressions(b, sideEffect);
                    
                    Chain<Unit> units = b.getUnits();
                    
                    HashMap<Value, Unit> availExprs = new HashMap<Value, Unit>();
                    
                    Iterator<Unit> stmtIt = units.snapshotIterator();
                    while (stmtIt.hasNext()) {
                        Stmt s = (Stmt) stmtIt.next();
//                        System.out.println("[Common Subexpression Elimination] : Analyzing source stmt - " + s);
                        
                        List availPairs = ae.getAvailablePairsBefore(s);
                        
                        // convert availPairs to HashMap
                        availExprs.clear();
                        Iterator availIt = availPairs.iterator();
                        while (availIt.hasNext())
                        {
                            UnitValueBoxPair up = (UnitValueBoxPair)availIt.next();
                            availExprs.put(new EquivalentValue(up.getValueBox().getValue()), up.getUnit());
                        }
//                        System.out.println("[Common Subexpression Elimination] Avail Expression: " + availExprs);
                        
                        if (s instanceof AssignStmt) {
                            Value rightValue = ((AssignStmt) s).getRightOp();
                            EquivalentValue erv = new EquivalentValue(rightValue);
                            
                            // only deal with expr
                            if(rightValue instanceof Expr && rightValue.getUseBoxes().size() > 1){
                                // rightValue exists in availExprs, replace with $cseTmp#
                                // ex. i = a + b; j = a + b;
                                //  => $cseTmp# = a + b; i = $cseTmp#; j = $cseTmp#;   
                                if(availExprs.containsKey(erv)){ 
                                    AssignStmt origStmt = (AssignStmt)availExprs.get(erv);
                                    Value leftOrigValue = origStmt.getLeftOp();
//                                    System.out.println("[Common Subexpression Elimination] LeftOrigValue is: " + leftOrigValue);
                                    
                                    if(leftOrigValue.toString().startsWith("$cseTmp")){
                                        // Directly replace rightValue with $cseTmp#.
                                        // ex. j = a + b; => j = $cseTmp#;  
                                        ((AssignStmt)s).setRightOp(leftOrigValue);
                                        System.out.println("[Common Subexpression Elimination] Replace rightValue with $cseTmp# and new statement is: " + s);
                                    } else {
                                        // create a local for temp storage
                                        String newName = "$cseTmp"+ counter; 
                                        counter++;
                                        Local l = Jimple.v().newLocal(newName, Type.toMachineType(rightValue.getType()));
                                        b.getLocals().add(l);
                                        
                                        // create a new statement for this local
                                        // ex. i = a + b; => $cseTmp# = a + b
                                        Value origLeftValue = origStmt.getLeftOp();
                                        origStmt.setLeftOp(l);
//                                        System.out.println("originStmt: " + origStmt);
                                        
                                        // i = $cseTmp#;
                                        Unit newStmt = Jimple.v().newAssignStmt(origLeftValue, l);
                                        units.insertAfter(newStmt, origStmt); 
                                        System.out.println("[Common Subexpression Elimination] Insert " + newStmt + " after originStmt");
                                        
                                        // Replace rightValue with $cseTmp#.
                                        // ex. j = a + b; => j = $cseTmp#;  
                                        ((AssignStmt)s).setRightOp(l);
                                        System.out.println("[Common Subexpression Elimination] Replace rightValue with $cseTmp# and new statement is: " + s + "\n");
                                    }
                                } 
                            }
                        }
                    }
                    System.out.println("[Common Subexpression Elimination] End - targetMethod : " + method);
                }        
                
                
                // 3. constant propagation
                public void constantPropagation(SootMethod method) {
                	boolean needToConstantFolding = false;
                	
                	do {
                		    needToConstantFolding = false;
                		    
	                        Body b = method.getActiveBody();
	                        
	                        /** Step1: Detect copy statements **/
	                        Chain<Unit> units = b.getUnits();
	                        
	                        // Get Flow Graph of units
	                        CompleteUnitGraph stmtGraph = new CompleteUnitGraph(b);
	                        
	                        // Provides an interface for querying for the definitions of a Local at a given Unit in a method
	                        // In other words, it will find a definition statement of local variable
	                        LocalDefs localDefs = new SimpleLocalDefs(stmtGraph);
	                        
	                        System.out.println("\n[Constant Propagation] Start - targetMethod : " + method);
	                        Iterator<Unit> stmtIt = units.snapshotIterator();
	                        while (stmtIt.hasNext()) {
	                            Stmt s = (Stmt) stmtIt.next();
	                            
	                            // Get iterator for DefBox - the left side of statement	
	                            Iterator<ValueBox> leftSideIt = s.getDefBoxes().iterator();
	                            
	                    		// Put initialized local variables into a map to processed unused variables 
	                            while (leftSideIt.hasNext()) {
	                            	ValueBox defBox = (ValueBox) leftSideIt.next();
	                            }
	
	                            
	                            // Get iterator for UseBox - the right side of statement
	                            Iterator<ValueBox> rightSideIt = s.getUseBoxes().iterator();
	
//	                            System.out.println("[Constant Propagation] Analyzing source stmt - " + s);
	                            
	                            while (rightSideIt.hasNext()) {
	                            	// Get rightside value. For example, it can be y, z, or y+z for "x = y + z"
	                            	ValueBox useBox = (ValueBox) rightSideIt.next();
	                            	//System.out.println("[Constant Propagation]  - useBox for that stmt : " + useBox.getValue());
	                            	
	                            	// If rightside of value if local variable
	                            	if (useBox.getValue() instanceof Local) {
	                            		
	                            		Local tmpLocal = (Local) useBox.getValue();
	                            		
	                            		// Get possible definition statement for that variable
	                            		List<Unit> defOftmpLocal = localDefs.getDefsOfAt(tmpLocal, s);
	                            		
	                            		// If it has just one definition statement - which means it can be replaced.
	                            		if (defOftmpLocal.size() == 1) {
	                                		//System.out.println("[Constant Propagation]  - Def stmt for the variable " + tmpLocal + " :" + defOftmpLocal.get(0));
	                            			
	                            			// Get Definition statement for that local variable
	                            			DefinitionStmt def = (DefinitionStmt) defOftmpLocal.get(0);
	                            			
	                            			// Get Right side of definition statement only when it's constant
	                            			if (def.getRightOp() instanceof Constant) {
	                            				if (useBox.canContainValue(def.getRightOp())) {
	                            					// Change this local's value to right side of definition statement - constant
	                            					System.out.println("[Constant Propagation]  - Old value " + useBox.getValue() + " is changed to : " + def.getRightOp());
	                            					useBox.setValue(def.getRightOp());
	                            					needToConstantFolding = true;
	                            				}
	                            			}
	                            		}
	                            		
	                            	}
	                            	
	                            }
	                       
	                        }                	
	                        System.out.println("[Constant Propagation] End - targetMethod : " + method);
                		
	                        if (needToConstantFolding)
	                        	constantFolding(method);
	                        
                	} while (needToConstantFolding);
                	
               	
                }                 
                
                
                // 4. copy propagation
                public void copyPropagation(SootMethod method) {
                    Body b = method.getActiveBody();
                    
                    /** Step1: Detect copy statements **/
                    Chain<Unit> units = b.getUnits();
                    
                    // Get Flow Graph of units
                    CompleteUnitGraph stmtGraph = new CompleteUnitGraph(b);
                    
                    // Provides an interface for querying for the definitions of a Local at a given Unit in a method
                    // In other words, it will find a definition statement of local variable
                    LocalDefs localDefs = new SimpleLocalDefs(stmtGraph);
                    
                    System.out.println("\n[Copy Propagation] Start - targetMethod : " + method);
                    Iterator<Unit> stmtIt = units.snapshotIterator();
                    while (stmtIt.hasNext()) {
                        Stmt s = (Stmt) stmtIt.next();
                        
                        // Get iterator for DefBox - the left side of statement	
                        Iterator<ValueBox> leftSideIt = s.getDefBoxes().iterator();
                        
                		// Put initialized local variables into a map to processed unused variables 
                        while (leftSideIt.hasNext()) {
                        	ValueBox defBox = (ValueBox) leftSideIt.next();
                        }

                        
                        // Get iterator for UseBox - the right side of statement
                        Iterator<ValueBox> rightSideIt = s.getUseBoxes().iterator();

//                        System.out.println("[Copy Propagation] Analyzing source stmt - " + s);
                        
                        while (rightSideIt.hasNext()) {
                        	// Get rightside value. For example, it can be y, z, or y+z for "x = y + z"
                        	ValueBox useBox = (ValueBox) rightSideIt.next();
                        	//System.out.println("[Copy Propagation]  - useBox for that stmt : " + useBox.getValue());
                        	
                        	// If rightside of value if local variable
                        	if (useBox.getValue() instanceof Local) {
                        		
                        		Local tmpLocal = (Local) useBox.getValue();
                        		
                        		// Get possible definition statement for that variable
                        		List<Unit> defOftmpLocal = localDefs.getDefsOfAt(tmpLocal, s);
                        		
                        		// If it has just one definition statement - which means it can be replaced.
                        		if (defOftmpLocal.size() == 1) {
                            		//System.out.println("[Copy Propagation]  - Def stmt for the variable " + tmpLocal + " : " + defOftmpLocal.get(0));
                        			
                        			// Get Definition statement for that local variable
                        			DefinitionStmt def = (DefinitionStmt) defOftmpLocal.get(0);
                        			
                        			// Get Right side of definition statement only when it's constant
                        			if (def.getRightOp() instanceof Local) {
                        				if (useBox.canContainValue(def.getRightOp())) {
                        					// Change this local's value to right side of definition statement - constant
                        					System.out.println("[Copy Propagation]  - Old variable " + useBox.getValue() + " is changed to : " + def.getRightOp());
                        					useBox.setValue(def.getRightOp());
                        					//System.out.println("[Copy Propagation]  - Old variable is changed to : " + def.getRightOp());
                        				}
                        			}
                        		}
                        		
                        	}
                        	
                        }
                   
                    }                	
                    System.out.println("[Copy Propagation] End - targetMethod : " + method);
                	
                }  
                
                // 5. dead assignment removal
                public void deadAssignmentElimination(SootMethod method) {
                	
                	// Get body
                	Body b = method.getActiveBody();

                	usedLocalVarMap.clear();
                	
                    /* Step1: Collect variable information - initialized, or used */
                    Chain<Unit> units = b.getUnits();
                    
                    System.out.println("\n[Dead Assignment Elimination] Start - targetMethod : " + method);
                    Iterator<Unit> stmtIt = units.snapshotIterator();
                    while (stmtIt.hasNext()) {
                        Stmt s = (Stmt) stmtIt.next();
                        
                        // Get iterator for DefBox - the left side of statement	
//                        Iterator<ValueBox> leftSideIt = s.getDefBoxes().iterator();
                        
                		// Put initialized local variables into the map 
//                        while (leftSideIt.hasNext()) {
//                        	ValueBox defBox = (ValueBox) leftSideIt.next();
//                        	if (defBox.getValue() instanceof Local) {
//                        		//usedLocalVarMap.put((Local)defBox.getValue(),(Local)defBox.getValue());
//                        	}
//                        }

                        
                        // Get iterator for UseBox - the right side of statement
                        Iterator<ValueBox> rightSideIt = s.getUseBoxes().iterator();
                        while (rightSideIt.hasNext()) {
                        	// Get rightside value. For example, it can be y, z, or y+z for "x = y + z"
                        	ValueBox useBox = (ValueBox) rightSideIt.next();
                        	
                        	// If rightside of value if local variable
                        	if (useBox.getValue() instanceof Local) {
                        		
                            	if (useBox.getValue() instanceof JArrayRef) {
                            		JArrayRef test = (JArrayRef)useBox.getValue();
                            		usedLocalVarMap.put(test.getBase(),test.getBase());
                            	}
                            	
                        		Local tmpLocal = (Local) useBox.getValue();
                        		
                        		// Put used local variables into the map 
                        		usedLocalVarMap.put(tmpLocal,tmpLocal);
                       		}
                       	}
                    }

                    /* Step2: Delete every assignments which are never used */
                    Iterator<Unit> stmtIt2 = units.snapshotIterator();
                    while (stmtIt2.hasNext()) {
                    	Stmt s = (Stmt) stmtIt2.next();

                        if (s instanceof AssignStmt) {
                            Value leftValue = ((AssignStmt) s).getLeftOpBox().getValue();
                            if (!leftValue.toString().startsWith("$") && usedLocalVarMap.get(leftValue) != leftValue) {
                            	if (leftValue instanceof JArrayRef) {
                            		JArrayRef test = (JArrayRef)leftValue;
                            		
                            		if (usedLocalVarMap.get(test.getBase()) != test.getBase() ) {
                                    	System.out.println("[Dead Assignment Elimination] stmt : " + s + " is removed.");
                                    	units.remove(s);
                            		}
                            	}
                            	else {
	                            	System.out.println("[Dead Assignment Elimination] stmt : " + s + " is removed.");
	                            	units.remove(s);
                            	}
                            }
                            	
                        }
                    }
                    System.out.println("[Dead Assignment Elimination] End - targetMethod : " + method);
                                  	
                	
                }                  
                
                // 6. constant folding
				public void constantFolding(SootMethod method) {
					final boolean printOut = false;
					Body b = method.getActiveBody();
					Chain<Unit> units = b.getUnits();

					if (printOut)
						System.out.println("\n[Constant Folding] Start - targetMethod : " + method);
//					if (printOut)
//						System.out.println("[Constant Folding] Units - " + units);

					Iterator<Unit> stmtIt = units
							.snapshotIterator();
					while (stmtIt.hasNext()) {
						Stmt s = (Stmt) stmtIt.next();
//						if (printOut)
//							System.out.println("[Constant Folding] stmt: " + s);

						List<ValueBox> lvb = s.getUseBoxes();
//						if (printOut)
//							System.out.println("[Constant Folding] - lvb size: " + lvb.size());

						for (int i = 0; i < lvb.size(); i++) {
//							if (printOut)
//								System.out.println("[Constant Folding] - lvb: " + lvb.get(i));

							// ex1. 'if $cseTmp0 <= 3 goto
							// $stack0#9 = 3 - 2'
							// -> lvb size: 3, lvb: [$cseTmp0],
							// [3], [$cseTmp0 <= 3]
							// ex2. 'c = (double) $stack0#6
							// -> lvb size: 2, lvb: [$stack0#6],
							// [(double) $stack0#6]
							if (i > 1) {
								Value valueTobeCalc = lvb
										.get(i).getValue();
								if (valueTobeCalc instanceof BinopExpr) {
									Value op1 = ((BinopExpr) valueTobeCalc).getOp1();
									Value op2 = ((BinopExpr) valueTobeCalc).getOp2();

									if ((op1 instanceof Constant)
											&& (op2 instanceof Constant)) {
										System.out.println("[Constant Folding] - - value to be calculated: " + valueTobeCalc);

										Value constValue = null;
										
										if (valueTobeCalc instanceof AddExpr)
											constValue = ((NumericConstant)op1).add((NumericConstant)op2);
										else if(valueTobeCalc instanceof SubExpr)
											constValue = ((NumericConstant)op1).subtract((NumericConstant)op2);
										else if(valueTobeCalc instanceof MulExpr)
											constValue = ((NumericConstant)op1).multiply((NumericConstant)op2);
										else if(valueTobeCalc instanceof DivExpr || valueTobeCalc instanceof RemExpr){
											// escaping the case of dividing by 0
											if (((op2 instanceof IntConstant) && ((IntConstant) op2).value == 0)
													|| ((op2 instanceof LongConstant) && ((LongConstant) op2).value == 0)
													|| ((op2 instanceof DoubleConstant) && ((DoubleConstant) op2).value == 0.0)
													|| ((op2 instanceof FloatConstant) && ((FloatConstant) op2).value == 0.0)) {
												continue;
											}
											else{
												if(valueTobeCalc instanceof DivExpr)
													constValue = ((NumericConstant)op1).divide((NumericConstant)op2);
												else
													constValue = ((NumericConstant)op1).remainder((NumericConstant)op2);
											}
										}
										else if (valueTobeCalc instanceof GtExpr)
											constValue = ((NumericConstant)op1).greaterThan((NumericConstant)op2);
							            else if (valueTobeCalc instanceof GeExpr)
							            	constValue = ((NumericConstant)op1).greaterThanOrEqual((NumericConstant)op2);
							            else if (valueTobeCalc instanceof LtExpr)
							            	constValue = ((NumericConstant)op1).lessThan((NumericConstant)op2);
							            else if (valueTobeCalc instanceof LeExpr)
							            	constValue = ((NumericConstant)op1).lessThanOrEqual((NumericConstant)op2);
							            else if (valueTobeCalc instanceof AndExpr)
							            	constValue = ((ArithmeticConstant)op1).and((ArithmeticConstant)op2);
							            else if (valueTobeCalc instanceof OrExpr)
							            	constValue = ((ArithmeticConstant)op1).or((ArithmeticConstant)op2);
							            else if (valueTobeCalc instanceof XorExpr)
							            	constValue = ((ArithmeticConstant)op1).xor((ArithmeticConstant)op2);
											
										if (constValue != null && lvb.get(i).canContainValue(constValue)) {
											System.out.println("[Constant Folding] - - type: " + constValue.getType() + ", calculated value: " + constValue);
											lvb.get(i).setValue(constValue);
										}
									}
								}
							}
						}
					}

					if (printOut)
						System.out.println("[Constant Folding] End - targetMethod : " + method);
				}
                
				// 7. conditional branch folding
		            public void conditionalBranchFolding(SootMethod method) {
		            	
		            	Body b = method.getActiveBody();	                              
		                Chain<Unit> units = b.getUnits();                      
		                Iterator<Unit> stmtIt = units.snapshotIterator(); 
		                
		                boolean conditionValue = false; // keep value of condition        
		                boolean canbeEvaluated = false; // check if we can evaluate condition Value at compile phase	            
		                
		                System.out.println("\n[Conditional Branch Folding] Start - targetMethod : " + method);		              
		                while (stmtIt.hasNext()) {
		                   Stmt s = (Stmt) stmtIt.next();  
		                   /* Check IfStmt and evaluate condition*/
		                   if(s instanceof IfStmt){                            
		                	   Value condition = ((IfStmt) s).getCondition();             
		                            
		                       if(condition instanceof ConditionExpr){                                
		                           Value op1 = ((ConditionExpr) condition).getOp1();
		                           Value op2 = ((ConditionExpr) condition).getOp2(); 
		                           if((op1 instanceof StringConstant || op1 instanceof DoubleConstant || op1 instanceof FloatConstant ||
		                               op1 instanceof IntConstant || op1 instanceof LongConstant)&&(op2 instanceof StringConstant || op2 instanceof DoubleConstant || op1 instanceof FloatConstant ||
		                               op2 instanceof IntConstant || op2 instanceof LongConstant)){
		                                
		                        	   canbeEvaluated = true;
		                                }
		                                /* check if we can evaluate the condition */
		                                if(canbeEvaluated)
		                                {
		                                	double left = Double.parseDouble(op1.toString());
		                                    double right = Double.parseDouble(op2.toString());                              
		                                   
		                                    if(condition instanceof EqExpr)
		                                    	conditionValue = (left == right);
		                                    else if(condition instanceof GeExpr)
		                                    	conditionValue = (left >= right);
		                                    else if(condition instanceof GtExpr)
		                                    	conditionValue = (left > right);
		                                    else if(condition instanceof LeExpr)
		                                    	conditionValue = (left <= right);
		                                    else if(condition instanceof LtExpr)
		                                    	conditionValue = (left < right);
		                                    else if(condition instanceof NeExpr)
		                                    	conditionValue = (left != right);   
		                                    
		                                      /*check value of compare*/
		                                    
		                                    if (conditionValue == true) {
		                                        // if condition of else always true, convert if to goto 
		                                    	System.out.println("[Conditional Branch Folding] else condition is true, change statement into target ");
		                                        Stmt newStmt = Jimple.v().newGotoStmt(((IfStmt)s).getTarget());  
		                                        System.out.println("[Conditional Branch Folding] Changing : " + s + " into: " + newStmt);
		                                        units.insertAfter(newStmt, s);
		                                        System.out.println("[Conditional Branch Folding] Removing statement : " + s);
		                                        units.remove(s);                         
		                                    	 }
		                                    else { 
		                                    	  // if condition of else is false remove both statement and target   
		                                    	  System.out.println("[Conditional Branch Folding] else condition is false, remove both target and statement");
		                                          units.remove(s);
		                                          /* check if contain else or not, if statement s does not contain "goto (branch)" --> statement contains else
		                                             we need to remove else block
		                                          */
		                                          if(!s.toString().contains("goto (branch)"))
		                                          {
		                                        	  System.out.println("[Conditional Branch Folding] Removing statement : " + s);
			                                          Stmt target = ((IfStmt)s).getTarget();
			                                          System.out.println("[Conditional Branch Folding] Removing target : " + target);
			                                          units.remove(target);
		                                          }                                
		                                          
		                                    	}
		                                	} // end canbeEvaluate
		                                	canbeEvaluated = false;
		                                }   // end ConditionExpr           
		                       		} // end IfStmt           
		                    } 
		                System.out.println("[Conditional Branch Folding] End - targetMethod : " + method);  
		                
		           }  
                // 8. unreachable code elimination
                public void unreachableCodeElimination(SootMethod method) {
                    Body b = method.getActiveBody();
                    
                    /** Step1: Detect copy statements **/
                    Chain<Unit> units = b.getUnits();
                     
                    // Get Flow Graph of units
                    CompleteUnitGraph stmtGraph = new CompleteUnitGraph(b);
                    System.out.println("\n[Unreachable Code Elimination] Start - targetMethod : " + method);
                    
                    List<Unit> headUnits = stmtGraph.getHeads();
                    List<Unit> tmpSucUnits = stmtGraph.getHeads();

                    Map<Unit, Unit> succOfHeadUnits = new HashMap<Unit, Unit>();
                    Map<Unit, Unit> unitsInControlFlow = new HashMap<Unit, Unit>();
                    Map<Unit, Unit> tmpUnits = new HashMap<Unit, Unit>();
                    
                    // Put heads into the Flow List
                    if (headUnits.size() > 0) {
	                    for (int i=0; i< headUnits.size(); i++) {

	                    	unitsInControlFlow.put(headUnits.get(i), headUnits.get(i));

	                    	// Get first Successor of heads
	                    	tmpSucUnits = stmtGraph.getSuccsOf(headUnits.get(i));
	                    	if (tmpSucUnits.size() > 0) {
		                    	for (int j=0; j<tmpSucUnits.size(); j++)
		                    		//if (tmpSucUnits.get(j) != null)
		                    		succOfHeadUnits.put(tmpSucUnits.get(j),tmpSucUnits.get(j));
	                    	}
//	                    	System.out.println("[Unreachable Code Elimination] Possible Starting point : " + headUnits.get(i));
	                    }
                    }
                    
                    // Put Successor into the Flow list
                    while (succOfHeadUnits.size() > 0) {
//                    	System.out.println("[Unreachable Code Elimination] successor size : " + succOfHeadUnits.size());
                    	tmpUnits.clear();

                    	int i = 0;
                    	for (Map.Entry<Unit, Unit> entry : succOfHeadUnits.entrySet()) { 
//                        	System.out.println("[Unreachable Code Elimination] successor stmt #" + (i+1) + " : " + entry.getKey());
                        	i++;
                        	
                        	if (unitsInControlFlow.get(entry.getKey()) == entry.getKey())
                        		continue;
                        	
                        	unitsInControlFlow.put(entry.getKey(),entry.getKey());
                        	tmpSucUnits = stmtGraph.getSuccsOf(entry.getKey());
                    		for (int j=0; j<tmpSucUnits.size(); j++)
	                    		tmpUnits.put(tmpSucUnits.get(j),tmpSucUnits.get(j));
                    		//unitsInControlFlow.putAll(tmpUnits);
                    	}
                    	
                    	succOfHeadUnits.clear();
                    	succOfHeadUnits.putAll(tmpUnits);
                    	
                    }

                    // Step2: Delete unreachable statements
                    Iterator<Unit> stmtIt2 = units.snapshotIterator();
                    while (stmtIt2.hasNext()) {
                    	Stmt s = (Stmt) stmtIt2.next();
                    	if (!unitsInControlFlow.containsKey(s)) {
                    		System.out.println("[Unreachable Code Elimination] Removing stmt : " + s);
                    		units.remove(s);
                    	}
                    }
                
                
                    System.out.println("[Unreachable Code Elimination] End - targetMethod : " + method + "\n");
                	                	
                	
                }   

 
                // 9. loop invariant motion
                public void loopInvariantMotion(SootMethod method, String phaseName, Map options){
                    System.out.println("\n[Loop Invariant Motion] Start - targetMethod : " + method);
                    
                    Body b = method.getActiveBody();
                    
                    Chain<Unit> units = b.getUnits();
                    
                    // Using soot API to find loop
                    LoopFinder lf = new LoopFinder();  
                    lf.transform(b, phaseName, options);

                    Collection<Loop> loops = lf.loops();
                    
                    // no loop invariants if no loops
                    if (loops.isEmpty()){
                    	System.out.println("[Loop Invariant Motion] End - targetMethod : " + method + "\n");
                    	return;
                    }
                    
                    Iterator<Loop> lIt = loops.iterator();
                    while (lIt.hasNext()){
                        Loop loop = lIt.next();
                        if(!(loop.getHead() instanceof IfStmt))
                            continue;
                        IfStmt header = (IfStmt)loop.getHead(); 
                        
//                        System.out.println("[Loop Invariant Motion] Loop header: " + header); 
                        
                        ValueBox vb = (ValueBox)header.getCondition().getUseBoxes().get(0);
                        Value condVar = vb.getValue();
                        
                        Stmt preStmt = getPreStmtOfLoop(units, loop.getLoopStatements());
                        
                        /** Step1: analyze each loop to get invariantVariables **/
                        Set<Value> usedVariables = new HashSet<Value>();
                        Set<Value> variantVariables = new HashSet<Value>();
                        List<UnitValueBoxPair> invariantVariables = new ArrayList<UnitValueBoxPair>(); 
                        
                        // get all the statements inside the loop
                        Collection<Stmt> loopStmts = loop.getLoopStatements();
                        int count = 0;
                        for(Stmt s : loopStmts){
                            System.out.println(s);
                            if (s instanceof GotoStmt){
                            	System.out.println("[Loop Invariant Motion] End - targetMethod : " + method + "\n");
                            	return;
                            }

                            if (s instanceof InvokeStmt){
                            	System.out.println("[Loop Invariant Motion] End - targetMethod : " + method + "\n");
                            	return; 
                            }
                            
                            if(s instanceof IfStmt){
                                IfStmt ifStmt = (IfStmt)s;
                                if(ifStmt.getTarget().toString().contains("invoke")){
                                	System.out.println("[Loop Invariant Motion] End - targetMethod : " + method + "\n");
                                	return;
                                }
                                
                                count++;
                                if(count == 2){
                                	System.out.println("[Loop Invariant Motion] End - targetMethod : " + method + "\n");
                                    return;
                                }
                            }
                        }
                        Iterator<Stmt> bIt = loopStmts.iterator();
                        int index = 0;
                        while (bIt.hasNext()){
                            Stmt singleStmt = bIt.next();
//                            System.out.println("[Loop Invariant Motion] : Analyzing source stmt - " + singleStmt);
                            if(index == 0){ // jump header statement
                                index++;
                                continue;
                            }
                            
//                            System.out.println("[Loop Invariant Motion] Loop stmt: "+ singleStmt);
                            if (singleStmt instanceof DefinitionStmt) {
                                DefinitionStmt ds = (DefinitionStmt)singleStmt;
                                if(isLeftOpVariable(ds.getLeftOp(), condVar, variantVariables))
                                    continue;
                                // update variantVariables and invariantVariables
                                if (ds.getRightOp() instanceof Constant){
                                    if (!usedVariables.contains(ds.getLeftOp())){
                                        usedVariables.add(ds.getLeftOp());
                                        UnitValueBoxPair up = new UnitValueBoxPair((Unit)singleStmt, ds.getLeftOpBox());
                                        invariantVariables.add(up);
                                    }
                                    else {
                                        // if using leftOp twice, this leftOp should be kept
                                        invariantVariables.remove(new UnitValueBoxPair((Unit)singleStmt, ds.getLeftOpBox()));
                                    }
                                }
                                if(ds.getRightOp() instanceof Local){ 
                                    Local l = (Local)ds.getRightOp();
                                    if(!l.equivTo(condVar)){
                                        if (!usedVariables.contains(ds.getLeftOp()) && !variantVariables.contains(l)){
                                            usedVariables.add(ds.getLeftOp());
                                            UnitValueBoxPair up = new UnitValueBoxPair((Unit)singleStmt, ds.getLeftOpBox());
                                            invariantVariables.add(up);
                                        }
                                        else {
                                            // if using leftOp twice, this leftOp should be kept
                                            invariantVariables.remove(new UnitValueBoxPair((Unit)singleStmt, ds.getLeftOpBox()));
                                            variantVariables.add((Local)ds.getLeftOp());
                                        }
                                    } else
                                        variantVariables.add((Local)ds.getLeftOp());
                                }
                                if(ds.getRightOp().getUseBoxes().size() > 1){
                                    Iterator it = ds.getRightOp().getUseBoxes().iterator();
                                    boolean moved = true;
                                    while(it.hasNext()){
                                        ValueBox tmp = (ValueBox)it.next();
                                        if(tmp.getValue() instanceof Local){
                                            Local l = (Local)tmp.getValue();
                                            // if conflict with condVar, we can't move this stmt
                                            if(l.equivTo(condVar) || variantVariables.contains(l)){
                                                variantVariables.add((Local)ds.getLeftOp());
                                                moved = false;
                                                break;
                                            }
                                        }
                                    }
                                    if(moved){  
                                        if (!usedVariables.contains(ds.getLeftOp())){
                                            usedVariables.add(ds.getLeftOp());
                                            UnitValueBoxPair up = new UnitValueBoxPair((Unit)singleStmt, ds.getLeftOpBox());
                                            invariantVariables.add(up);
                                        }
                                        else {
                                            // if using leftOp twice, this leftOp should be kept
                                            invariantVariables.remove(new UnitValueBoxPair((Unit)singleStmt, ds.getLeftOpBox()));
                                            variantVariables.add((Local)ds.getLeftOp());
                                        }
                                    }
                                }
                            }
                        }
                        /** Step2: move Invariant variable out of loop **/
                        // ex. goto label1;
                        //     label0: x =10; i = i + 1;
                        //     label1: if i < 5 goto label0;
                        // =>   
                        //     x= 10; goto label1;
                        //     label0: i = i + 1;
                        //     label1: if i < 5 goto label0;
                        for(UnitValueBoxPair up : invariantVariables){
                            Stmt stmt = (Stmt)up.getUnit();
//                            System.out.println("[Loop Invariant Motion] Current Units: " + units);
                            
                            // remove invariant stmt,
                            // in the above ex., remove x = 10
                            units.remove(stmt);  
                            System.out.println("[Loop Invariant Motion] Remove stmt: " + stmt);
                            System.out.println("[Loop Invariant Motion] After remove, Units become: " + units);
                            
                            // insert invariant stmt before loop
                            // in the above ex., insert x = 10 before 'goto label1' 
                            units.insertBefore(stmt, preStmt);
                            System.out.println("[Loop Invariant Motion] Insert " + stmt + " before " + preStmt);
                            System.out.println("[Loop Invariant Motion] After insert, Units become: " + units);
                        }
                    }
                    
                    System.out.println("[Loop Invariant Motion] End - targetMethod : " + method + "\n");
                }
                
                /**
                 * get previous statement of loop header -- ex. goto loop-label
                 * @param units
                 * @param loopStmts
                 * @return
                 */
                public Stmt getPreStmtOfLoop(Chain<Unit> units, Collection<Stmt> loopStmts){
                    Iterator<Stmt> bIt = loopStmts.iterator();
                    
                    int index = 0;
                    while (bIt.hasNext()){
                        Stmt singleStmt = bIt.next();
                        if(index == 0){ // jump header statement
                            index++;
                            continue;
                        }
                        return (Stmt) units.getPredOf(singleStmt);
                    }
                    return null;
                }
                
                public boolean isLeftOpVariable(Value leftOp, Value condVar, Set<Value> variantVariables){
                    if(leftOp instanceof ArrayRef){
                        ArrayRef array = (ArrayRef)leftOp;
                        Value index = array.getIndex();   
                        return index.equivTo(condVar) || variantVariables.contains(index);
                    }
                    return false;
                }
                
                
                // 10. value range optimization
                public void valueRangeOptimization(SootMethod method, String phaseName, Map options){
                    System.out.println("\n[Value Range Optimization] Start - targetMethod : " + method);
                    
                    Body b = method.getActiveBody();
                    
                    Chain<Unit> units = b.getUnits();
                    
                    CompleteUnitGraph stmtGraph = new CompleteUnitGraph(b);
                    
                    LocalDefs localDefs = new SimpleLocalDefs(stmtGraph);
                    
                    // Using soot API to find loop
                    LoopFinder lf = new LoopFinder();  
                    lf.transform(b, phaseName, options);

                    Collection<Loop> loops = lf.loops();
                    
                    // no loop invariants if no loops
                    if (loops.isEmpty()){
                    	System.out.println("[Value Range Optimization] End - targetMethod : " + method + "\n");
                    	return;
                    }
                    
                    Iterator<Loop> lIt = loops.iterator();
                    while (lIt.hasNext()){
                        Loop loop = lIt.next();
                        
                        if(!(loop.getHead() instanceof IfStmt))
                            continue;
                        IfStmt header = (IfStmt)loop.getHead(); 
                        ConditionExpr loopCondition2 = (ConditionExpr)header.getCondition();
                        Unit loopConditionUnit1 = localDefs.getDefsOfAt((Local)loopCondition2.getOp1(), (Unit)header).get(0);
                        
                        // get condVar
                        ValueBox vb = (ValueBox)header.getCondition().getUseBoxes().get(0);
                        Value condVar = vb.getValue();
                        
//                        System.out.println("[Value Range Optimization] LoopCondition1: " + loopConditionUnit1);
//                        System.out.println("[Value Range Optimization] LoopCondition2: " + loopCondition2);
                        
                        // get all the statements inside the loop
                        Collection<Stmt> loopStmts = loop.getLoopStatements();
                        Iterator<Stmt> bIt = loopStmts.iterator();
                        int index = 0;
                        while (bIt.hasNext()){
                            Stmt singleStmt = bIt.next();
                            
                            if(index == 0){ // jump header statement
                                index++;
                                continue;
                            }
//                            System.out.println("[Value Range Optimization] Analyzing source stmt - "+ singleStmt);
                            
                            if(singleStmt instanceof IfStmt){
                                IfStmt ifStmt = (IfStmt)singleStmt;
                                ConditionExpr ifCondition = (ConditionExpr)ifStmt.getCondition();
//                                System.out.println("[Value Range Optimization] IfCondition: " + ifCondition); 
                               
                                if(!ifCondition.getOp2().toString().contains("[0-9]+"))
                                    continue;
                                double op2 = Double.parseDouble(ifCondition.getOp2().toString());
//                                System.out.println("[Value Range Optimization] op2: " + op2);  
                                if(ifCondition.getOp1().equivTo(condVar) && ifCondition.getOp2() instanceof Constant){
                                    if(ifCondition instanceof GeExpr || ifCondition instanceof GtExpr){
                                        double rightRange = Double.parseDouble(loopCondition2.getOp2().toString());
//                                        System.out.println("[Value Range Optimization] rightRange: " + rightRange); 
                                        // if maximun(in loop) <= compared variable(in ifStmt), ifStmt is always true
                                        if(rightRange <= op2){
                                            units.remove(ifStmt);
                                            System.out.println("[Value Range Optimization] Remove ifStmt: " + ifStmt);
                                        }
                                    }
                                    if(ifCondition instanceof LeExpr || ifCondition instanceof LtExpr){
                                        double leftRange = Double.parseDouble(loopConditionUnit1.getUseBoxes().get(0).getValue().toString());
//                                        System.out.println("[Value Range Optimization] leftRange: " + leftRange); 
                                        // if minimum(in loop) >= compared variable(in ifStmt), ifStmt is always true
                                        if(leftRange >= op2){
                                            units.remove(ifStmt);
                                            System.out.println("[Value Range Optimization] Remove ifStmt: " + ifStmt);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    System.out.println("[Value Range Optimization] End - targetMethod : " + method + "\n");
                }
                
                public void processOptimization(SootMethod targetMethod, String phaseName, Map options) {
                        /*      1. uninitialized local elimination
    							2. common subexpression elimination
    							3. constant propagation 
    							4. copy propagation
    							5. dead assignment removal
    							6. constant folding
    							7. loop invariant optimization
    							8. conditional branch folding
    							9. unreachable code elimination
    				    */
                        
                        System.out.println("[CS243 Project] Applying optimization for " + targetMethod + " - start");
                        
    					if (removeUn == 1)
    						removeUninitiailizedLocal(targetMethod);
    					if (removecommonSub == 1)
    						commonSubexpressionElimination(targetMethod);
    					if (constantProp == 1)
    						constantPropagation(targetMethod);
    					if (copyProp == 1)
    						copyPropagation(targetMethod);
    					if (deadAssign == 1)
    						deadAssignmentElimination(targetMethod);
    					if (constantFold == 1)
    						constantFolding(targetMethod);
    					if (loopInvariant == 1)
    						loopInvariantMotion(targetMethod, phaseName, options);
    					if (valueRange == 1)
    						valueRangeOptimization(targetMethod, phaseName, options);
    					if (conditionBranch == 1)
    						conditionalBranchFolding(targetMethod);
    					if (unreachbleCode == 1)
    						unreachableCodeElimination(targetMethod);
    					
                        System.out.println("[CS243 Project] Applying optimization for " + targetMethod + " - end" + "\n");
                }
                
            }));     
            soot.Main.main(args);
            System.out.println("\n[CS243 Project] Optimization End ================================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
