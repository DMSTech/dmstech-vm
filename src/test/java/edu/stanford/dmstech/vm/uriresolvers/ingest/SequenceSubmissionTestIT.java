package edu.stanford.dmstech.vm.uriresolvers.ingest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import org.junit.Test;

public class SequenceSubmissionTestIT {

	String sampleSequence = "_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7feb <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fff .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7feb <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-18> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ffe <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fea .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ffe <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-36> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-13> <http://purl.org/dc/elements/1.1/title> \"f. 99bisv\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-13> <http://www.w3.org/2003/12/exif/ns#height> \"5575\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-13> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-13> <http://www.w3.org/2003/12/exif/ns#width> \"4039\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-7> <http://purl.org/dc/elements/1.1/title> \"f. 13v\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-7> <http://www.w3.org/2003/12/exif/ns#height> \"5467\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-7> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-7> <http://www.w3.org/2003/12/exif/ns#width> \"4063\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-26> <http://purl.org/dc/elements/1.1/title> \"f. 94r\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-26> <http://www.w3.org/2003/12/exif/ns#height> \"5731\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-26> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-26> <http://www.w3.org/2003/12/exif/ns#width> \"4183\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-1> <http://purl.org/dc/elements/1.1/title> \"f. 8r\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-1> <http://www.w3.org/2003/12/exif/ns#height> \"5575\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-1> <http://www.w3.org/2003/12/exif/ns#width> \"4019\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe4 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff0 .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe4 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-28> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fdd <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff6 .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fdd <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-20> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-20> <http://purl.org/dc/elements/1.1/title> \"f. 9v\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-20> <http://www.w3.org/2003/12/exif/ns#height> \"5503\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-20> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-20> <http://www.w3.org/2003/12/exif/ns#width> \"3943\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff7 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe9 .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff7 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-8> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-33> <http://purl.org/dc/elements/1.1/title> \"f. 67r\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-33> <http://www.w3.org/2003/12/exif/ns#height> \"5552\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-33> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-33> <http://www.w3.org/2003/12/exif/ns#width> \"4015\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-18> <http://purl.org/dc/elements/1.1/title> \"f. 18v\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-18> <http://www.w3.org/2003/12/exif/ns#height> \"5611\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-18> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-18> <http://www.w3.org/2003/12/exif/ns#width> \"3859\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe6 .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-31> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fea <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff5 .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fea <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-30> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ffd <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ffc .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ffd <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-15> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-12> <http://purl.org/dc/elements/1.1/title> \"f. 11v\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-12> <http://www.w3.org/2003/12/exif/ns#height> \"5515\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-12> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-12> <http://www.w3.org/2003/12/exif/ns#width> \"3871\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-6> <http://purl.org/dc/elements/1.1/title> \"f. 8v\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-6> <http://www.w3.org/2003/12/exif/ns#height> \"5467\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-6> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-6> <http://www.w3.org/2003/12/exif/ns#width> \"3728\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe9 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe8 .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe9 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-7> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-25> <http://purl.org/dc/elements/1.1/title> \"f. 94v\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-25> <http://www.w3.org/2003/12/exif/ns#height> \"5635\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-25> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-25> <http://www.w3.org/2003/12/exif/ns#width> \"4194\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe3 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe2 .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe3 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-2> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff6 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff8 .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff6 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-11> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fef <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fdf .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fef <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-26> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-32> <http://purl.org/dc/elements/1.1/title> \"f. 4r\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-32> <http://www.w3.org/2003/12/exif/ns#height> \"5528\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-32> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-32> <http://www.w3.org/2003/12/exif/ns#width> \"3931\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-17> <http://purl.org/dc/elements/1.1/title> \"f. 18r\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-17> <http://www.w3.org/2003/12/exif/ns#height> \"5491\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-17> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-17> <http://www.w3.org/2003/12/exif/ns#width> \"3823\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fef .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-27> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ffc <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ffc <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-16> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-11> <http://purl.org/dc/elements/1.1/title> \"f. 11r\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-11> <http://www.w3.org/2003/12/exif/ns#height> \"5503\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-11> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-11> <http://www.w3.org/2003/12/exif/ns#width> \"3943\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-5> <http://purl.org/dc/elements/1.1/title> \"f. 16bisv\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-5> <http://www.w3.org/2003/12/exif/ns#height> \"5888\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-5> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-5> <http://www.w3.org/2003/12/exif/ns#width> \"3925\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe8 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe7 .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe8 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-10> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence.xml> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.openarchives.org/ore/terms/ResourceMap> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence.xml> <http://www.openarchives.org/ore/terms/describes> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence.xml> <http://purl.org/dc/elements/1.1/format> \"application/rdf+xml\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-24> <http://purl.org/dc/elements/1.1/title> \"f. 17r\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-24> <http://www.w3.org/2003/12/exif/ns#height> \"5611\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-24> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-24> <http://www.w3.org/2003/12/exif/ns#width> \"3907\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe0 .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-3> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff5 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff4 .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff5 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-29> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-31> <http://purl.org/dc/elements/1.1/title> \"f. 4v\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-31> <http://www.w3.org/2003/12/exif/ns#height> \"5527\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-31> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-31> <http://www.w3.org/2003/12/exif/ns#width> \"3945\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fee <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fed .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fee <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-33> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-16> <http://purl.org/dc/elements/1.1/title> \"f. 104v\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-16> <http://www.w3.org/2003/12/exif/ns#height> \"5515\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-16> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-16> <http://www.w3.org/2003/12/exif/ns#width> \"3931\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-29> <http://purl.org/dc/elements/1.1/title> \"f. 21v\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-29> <http://www.w3.org/2003/12/exif/ns#height> \"5492\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-29> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-29> <http://www.w3.org/2003/12/exif/ns#width> \"3992\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ffb <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ffd .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ffb <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-13> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-10> <http://purl.org/dc/elements/1.1/title> \"f. 14r\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-10> <http://www.w3.org/2003/12/exif/ns#height> \"5503\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-10> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-10> <http://www.w3.org/2003/12/exif/ns#width> \"3907\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-4> <http://purl.org/dc/elements/1.1/title> \"f. 16bisr\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-4> <http://www.w3.org/2003/12/exif/ns#height> \"5842\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-4> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-4> <http://www.w3.org/2003/12/exif/ns#width> \"4085\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe7 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe3 .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe7 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-9> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-23> <http://purl.org/dc/elements/1.1/title> \"f. 17v\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-23> <http://www.w3.org/2003/12/exif/ns#height> \"5599\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-23> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-23> <http://www.w3.org/2003/12/exif/ns#width> \"3859\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence>     <http://www.shared-canvas.org/ns/hasOptimizedSerialization> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/optimized/OriginalSequence> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-27> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-20> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-32> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-30> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-24> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-36> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-5> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-3> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.openarchives.org/ore/terms/Aggregation> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-12> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-32> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-18> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-16> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-21> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-13> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-33> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-25> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-8> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-6> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-19> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-1> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Sequence> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-10> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff1 .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-22> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-14> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/1999/02/22-rdf-syntax-ns#List> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-34> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-28> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-26> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-31> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-9> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-7> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-35> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-11> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-23> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-4> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-17> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-2> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-15> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence> <http://www.openarchives.org/ore/terms/aggregates> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-29> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-36> <http://purl.org/dc/elements/1.1/title> \"f. 19v\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-36> <http://www.w3.org/2003/12/exif/ns#height> \"3840\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-36> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-36> <http://www.w3.org/2003/12/exif/ns#width> \"2767\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ffa .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-5> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff4 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff3 .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff4 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-22> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-30> <http://purl.org/dc/elements/1.1/title> \"f. 21r\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-30> <http://www.w3.org/2003/12/exif/ns#height> \"5504\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-30> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-30> <http://www.w3.org/2003/12/exif/ns#width> \"3884\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fed <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe4 .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fed <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-34> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-15> <http://purl.org/dc/elements/1.1/title> \"f. 104r\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-15> <http://www.w3.org/2003/12/exif/ns#height> \"5527\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-15> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-15> <http://www.w3.org/2003/12/exif/ns#width> \"4027\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-9> <http://purl.org/dc/elements/1.1/title> \"f. 14v\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-9> <http://www.w3.org/2003/12/exif/ns#height> \"5491\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-9> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-9> <http://www.w3.org/2003/12/exif/ns#width> \"3859\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-28> <http://purl.org/dc/elements/1.1/title> \"f. 93r\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-28> <http://www.w3.org/2003/12/exif/ns#height> \"5695\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-28> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-28> <http://www.w3.org/2003/12/exif/ns#width> \"3943\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ffa <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff9 .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ffa <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-24> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-3> <http://purl.org/dc/elements/1.1/title> \"f. 16v\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-3> <http://www.w3.org/2003/12/exif/ns#height> \"5888\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-3> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-3> <http://www.w3.org/2003/12/exif/ns#width> \"4016\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe6 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe5 .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe6 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-1> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-22> <http://purl.org/dc/elements/1.1/title> \"f. 23r\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-22> <http://www.w3.org/2003/12/exif/ns#height> \"5611\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-22> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-22> <http://www.w3.org/2003/12/exif/ns#width> \"3979\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fdf <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff2 .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fdf <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-25> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff9 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fec .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff9 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-23> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-35> <http://purl.org/dc/elements/1.1/title> \"f. 19r\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-35> <http://www.w3.org/2003/12/exif/ns#height> \"3840\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-35> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-35> <http://www.w3.org/2003/12/exif/ns#width> \"2767\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe1 .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-4> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff3 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fee .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff3 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-21> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fec <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7feb .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fec <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-17> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fff <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ffe .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fff <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-35> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-14> <http://purl.org/dc/elements/1.1/title> \"f. 99bisr\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-14> <http://www.w3.org/2003/12/exif/ns#height> \"5587\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-14> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-14> <http://www.w3.org/2003/12/exif/ns#width> \"4099\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-8> <http://purl.org/dc/elements/1.1/title> \"f. 13r\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-8> <http://www.w3.org/2003/12/exif/ns#height> \"5431\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-8> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-8> <http://www.w3.org/2003/12/exif/ns#width> \"3990\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-27> <http://purl.org/dc/elements/1.1/title> \"f. 93v\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-27> <http://www.w3.org/2003/12/exif/ns#height> \"5599\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-27> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-27> <http://www.w3.org/2003/12/exif/ns#width> \"4003\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-2> <http://purl.org/dc/elements/1.1/title> \"f. 16r\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-2> <http://www.w3.org/2003/12/exif/ns#height> \"6573\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-2> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-2> <http://www.w3.org/2003/12/exif/ns#width> \"4344\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe5 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fde .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fe5 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-6> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-21> <http://purl.org/dc/elements/1.1/title> \"f. 23v\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-21> <http://www.w3.org/2003/12/exif/ns#height> \"5587\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-21> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-21> <http://www.w3.org/2003/12/exif/ns#width> \"3883\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fde <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fdd .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7fde <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-19> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff8 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff7 .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff8 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-12> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-34> <http://purl.org/dc/elements/1.1/title> \"f. 67v\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-34> <http://www.w3.org/2003/12/exif/ns#height> \"5588\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-34> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-34> <http://www.w3.org/2003/12/exif/ns#width> \"3955\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-19> <http://purl.org/dc/elements/1.1/title> \"f. 9r\" .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-19> <http://www.w3.org/2003/12/exif/ns#height> \"5551\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-19> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.shared-canvas.org/ns/Canvas> .\n" + 
			"<http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-19> <http://www.w3.org/2003/12/exif/ns#width> \"3883\"^^<http://www.w3.org/2001/XMLSchema#integer> .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> _:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ffb .\n" + 
			"_:AX2dX5092362eX3aX136f01c3c15X3aXX2dX7ff2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://localhost:8080/dms/sc/Stanford/kq131cs7229/canvas-14> .\n";
	
	
	@Test
	public void testSubmitNewSequence() {
		 given().
		 	formParam("makedefault", "true").
		 	formParam("sequence", sampleSequence).
	       
         expect().
         	statusCode(201).
         	header("Location", containsString("/dms/sc/Stanford/kq131cs7229/sequences/")).
         when().
         post("/dms/sc/Stanford/kq131cs7229/sequences");
	}

	
	

}
