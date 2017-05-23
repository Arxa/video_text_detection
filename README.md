<img src="https://travis-ci.org/Arxa/VideoText_Extractor.svg?branch=master">
<br>
<br>

```diff
+ [This Project is currently on-going]
```
<br>
<a href="http://www.teicm.gr/index.php?lang=en" target="_blank"> <img src="tei.png" width="500" height="400" align="middle"> </a>
<br>
<br>

<h2>Department of Engineering Informatics</h2>
<br>

<h2>Thesis Title</h2>
"Text Extraction from Complex Video Scenes"

<h2>Supervisor</h2>
Dr. Athanasios Nikolaidis, nikolaid@teiser.gr

<h2>Description</h2>
<p>The objective of this Thesis is the development of high quality Image Processing software for detection and extraction of text displays in video scenes with a complex background. Our approach combines methodologies and techniques presented in popular scientific papers while it also implements custom algorithms and ideas in a highly advanced software development environment.<br> <br>
 <a href="https://www.hindawi.com/journals/mpe/2016/2187647/">"<i>A Method of Effective Text Extraction for Complex Video Scene</i>"</a>,<br>
 <a href="http://ieeexplore.ieee.org/document/5557889/">"<i>A Laplacian Approach to Multi-Oriented Text Detection in Video</i>"</a>,<br>
 <a href="https://www.researchgate.net/publication/220860334_A_Laplacian_Method_for_Video_Text_Detection">"<i>A Laplacian Method for Video Text Detection</i>"</a>,<br>
 <a href="https://www.researchgate.net/publication/223882980_A_new_robust_algorithm_for_video_text_extraction">"<i>A new robust algorithm for video text extraction</i>"</a><br>
 
 Advanced <b>Image Processing</b> and <b>Machine Learning</b> techniques have been used for this software
 along with a fully professional code design and development environment, in order to produce a stable, reusable, extensible and well tested application.
 

  <i>This project is currently on-going; the Thesis's detailed technical PDF is soon to be added here.</i> </p>
  
  
  <h2>Text Extraction Steps</h2>
  
  <ul>
        <li>
            Load video file locally or capture it through computer's webcam
        </li>
        <li>
            Grab video frame using a step (i.e. 1 every 5 frames)
        </li>
        <li>
            Apply the Gaussian Blurred Filter
        </li>
        <li>
            Convert to Gray Scale
        </li>
        <li>
            Apply the Laplacing Filter
        </li>
        <li>
            Construct a new image based on the MGD operator values
        </li>
        <li>
            Normalize the pixel values to [0,1]
        </li>
        <li>
            Apply the K-Means Clustering with cluster labels of '0' & '1'
        </li>
        <li>
            Construct a binary image with cluster label '1' as white pixels
        </li>
        <li>
            Apply the morphological operator Dilation on white pixels
        </li>
        <li>
            Filter the dilated blocks based on Connected Components labeling
        </li>
        <li>
            Apply further filtering using Sobel Edges and block Size
        </li>
        <li>
             Now we have the Text Regions of our frame
        </li>
        <li>
             Crop the text region images
        </li>
        <li>
             Preprocess the images using the 'Unsharp Mask' Filter
        </li>
        <li>
             Apply OCR and get the corresponding text
        </li>
  
 </ul>    
  
  

<h2>Technologies used</h2>
  
  <li>
      <b>Programming language</b>
      <ul>
        <li>
            Java 
        </li>
      </ul>    
  </li>
  
  <li>
      <b>Libraries</b>
      <ul>
        <li>
            JavaFX
        </li>
        <li>
            OpenCV
        </li>
        <li>
            JavaCPP-Tesseract
        </li>
        <li>
            Guava
        </li>
        <li>
            Colt
        </li>
      </ul>    
  </li>
  
  <li>
      <b>Frameworks</b>
      <ul>
        <li>
            JUnit
        </li>
        <li>
            TestFX
        </li>
      </ul>    
  </li>
  
  <li>
      <b>Tools (Other Technologies)</b>
      <ul>
        <li>
            InteliJ IDEA
        </li>    
        <li>
            Gradle
        </li>
        <li>
            Ehcache
        </li>
        <li>
            Travis CI
        </li>
        <li>
            Git
        </li>
        <li>
            CSS
        </li>
      </ul>    
  </li>
  <li>
      <b>Design Pattern</b>
      <ul>
        <li>
            Model–View–Controller (MVC)
        </li>
      </ul>    
  </li>
  
</ul>
