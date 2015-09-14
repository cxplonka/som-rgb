# som-rgb

This project contains a java implementation of the Self Organizing Maps (SOM) algorithm, based on an 
RGB color space. For more information on the RGB example refer to [ai-junkie.com](http://www.ai-junkie.com/ann/som/som1.html).

The visualisation is done on an 2D Plane and an 3D RGB Cube based on a Java3D implementation.

![](https://github.com/cxplonka/som-rgb/blob/master/screenshot.png)

## SOM - Wikipedia

A self-organizing map (SOM) or self-organizing feature map (SOFM) is a type of artificial neural network (ANN) 
that is trained using unsupervised learning to produce a low-dimensional (typically two-dimensional), discretized 
representation of the input space of the training samples, called a map. Self-organizing maps are different from 
other artificial neural networks in the sense that they use a neighborhood function to preserve the topological 
properties of the input space.

This makes SOMs useful for visualizing low-dimensional views of high-dimensional data, akin to multidimensional scaling. 
The artificial neural network introduced by the Finnish professor Teuvo Kohonen in the 1980s is sometimes called a 
Kohonen map or network.

(https://en.wikipedia.org/wiki/Self-organizing_map)

## Running

    // Build application with and run som-rgb.bat
    gradle installDist

## License

Free to use, just drop a note. I also would appreciate it if you give me credit where due.