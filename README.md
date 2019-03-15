<a href="http://www.teicm.gr/index.php?lang=en" target="_blank"> <img src="https://user-images.githubusercontent.com/15330998/40585514-e8f8ee18-61bc-11e8-8ff7-94f98cc88541.png" width="500" height="400" align="middle"> </a>
<br>

<h2>Department of Engineering Informatics</h2>
<br>
<h3>Thesis Title</h3>
Text extraction from complex video scenes

<h3>Supervisor</h3>
Dr. Athanasios Nikolaidis, nikolaid@teiser.gr

<h3>Objective</h3>
The objective of my Thesis was the development of an Image Processing desktop application, capable of detecting and extracting text displays from videos with complex backgrounds. My approach implements Machine Learning and Image Analysis methodologies from various popular scientific papers. 

<h3>Technologies</h3>
JavaFX, OpenCV, LIBSVM, Gradle, JUnit, TestFX

<h3>References</h3>

[1] Palaiahnakote Shivakumara, Trung Quy Phan and Chew Lim Tan, Senior Member, IEEE “A   Laplacian Approach to Multi-Oriented Text Detection in Video”, IEEE

[2]	Trung Quy Phan, Palaiahnakote Shivakumara and Chew Lim Tan “A Laplacian Method for Video Text Detection”, School of Computing, National University of Singapore, 2009

[3]	Rakesh Mehta, Karen Egiazarian, “Rotated Local Binary Pattern (RLBP) Rotation invariant texture descriptor”, 2nd International Conference on Pattern Recognition Applications and Methods, ICPRAM 2013, Barcelona, Spain, 2013

[4]	Cong Yao, “MSRA Text Detection 500 Database (MSRA-TD500)”, Huazhong University of Science and Technology, 2012

[5]	Visual Geometry Group, “Synthetic Word Dataset ”, Department of Engineering Science, University of Oxford


<a href="https://github.com/Arxa/VideoText_Extractor/files/2042676/Thesis_Nikiforos_Archakis_10.pdf"> <h3>Download my Thesis [PDF-Greek]</h3>
 </a>

<br>
<br>

<h2>Text Detection</h2>

<h3>Original Frame</h3>
<img src="https://user-images.githubusercontent.com/15330998/40585714-17ed8f5a-61c0-11e8-9fb5-d7cc232c8d34.png" align="middle">

<h3>Gaussian Filter</h3>
<img src="https://user-images.githubusercontent.com/15330998/40585716-183f3c4c-61c0-11e8-84bb-14e0a7ac9407.png" align="middle">

<h3>Grayscale</h3>
<img src="https://user-images.githubusercontent.com/15330998/40585718-18961d6e-61c0-11e8-852a-d6a91f3da296.png" align="middle">

<h3>Laplacian Filter</h3>
<img src="https://user-images.githubusercontent.com/15330998/40585720-18ea5974-61c0-11e8-9942-0d0e8232a155.png" align="middle">

<h3>Maximum Gradient Difference</h3>
<img src="https://user-images.githubusercontent.com/15330998/40585721-1917e358-61c0-11e8-9f56-e7b6e7cec00c.png" align="middle">

<h3>Binarization</h3>
<img src="https://user-images.githubusercontent.com/15330998/40585722-19465dbe-61c0-11e8-82f2-d8074af5bc5f.png" align="middle">

<h3>Dilation</h3>
<img src="https://user-images.githubusercontent.com/15330998/40585723-196e8d3e-61c0-11e8-822c-910253430fbe.png" align="middle">

<h3>Connected Components</h3>
<img src="https://user-images.githubusercontent.com/15330998/40585724-19963280-61c0-11e8-98cd-565fc8e06bcb.png" align="middle">

<h3>1st Filter - Removing components with bigger height than width</h3>
<img src="https://user-images.githubusercontent.com/15330998/40585715-181690da-61c0-11e8-98ec-c3e945de5532.png" align="middle">

<h3>2nd Filter - Removing components with small area</h3>
<img src="https://user-images.githubusercontent.com/15330998/40585717-186b2d8e-61c0-11e8-8e52-20e2f7695802.png" align="middle">

<h3>3rd Filter - Classifying text areas using Support Vector Machines</h3>
<img src="https://user-images.githubusercontent.com/15330998/40585719-18c05c1e-61c0-11e8-8385-f303e6285396.png" align="middle">

<br>
<br>
<h2>Text Extraction</h2>

<h3>Cropped Text Area</h3>
<img src="https://user-images.githubusercontent.com/15330998/40586485-3ea83c5c-61cb-11e8-8323-f5684860aa54.png" align="middle">

<h3>Grayscale</h3>
<img src="https://user-images.githubusercontent.com/15330998/40586486-3ed1c14e-61cb-11e8-8de4-14fc50d5bc6e.png" align="middle">

<h3>Unsharp Masking</h3>
<img src="https://user-images.githubusercontent.com/15330998/40586487-3efa94ca-61cb-11e8-9113-e31b5d59685b.png" align="middle">

<h3>Otsu Binarization</h3>
<img src="https://user-images.githubusercontent.com/15330998/40586488-3f272c06-61cb-11e8-86e3-7cd524d20d35.png" align="middle">

<h3>Apply OCR</h3>

<br>
<br>

<h2>JavaFX Application</h2>

<h3>Main View</h3>
<img src="https://user-images.githubusercontent.com/15330998/40586011-def767bc-61c3-11e8-8cc6-7606b73cc849.png" align="middle">

<h3>Settings</h3>
<img src="https://user-images.githubusercontent.com/15330998/40586013-df4ffcb0-61c3-11e8-87f0-bbece05c7f51.png" align="middle">

<h3>Choosing video file</h3>
<img src="https://user-images.githubusercontent.com/15330998/40586008-de7a2cb6-61c3-11e8-9f1f-a383e145b35a.png" align="middle">

<h3>Text Extraction</h3>
<img src="https://user-images.githubusercontent.com/15330998/40586014-df7cd5c8-61c3-11e8-8541-87d9089f2850.png" align="middle">

<h3>Text Detection</h3>
<img src="https://user-images.githubusercontent.com/15330998/40586175-df2d8790-61c6-11e8-88ab-64aa15b85e81.gif" align="middle">
<img src="https://user-images.githubusercontent.com/15330998/40586140-0e7be89e-61c6-11e8-90d8-378f90b72546.gif" align="middle">
<img src="https://user-images.githubusercontent.com/15330998/40586325-5d39deac-61c9-11e8-863e-1a4f31255fbc.gif" align="middle">


