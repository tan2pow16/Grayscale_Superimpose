package tan2pow16;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * @author tan2pow16
 * 
 *  Project inspired by this youtube video:
 *  https://www.youtube.com/watch?v=LeinMHbYbJI
 *  
 *  I "improved" the method mentioned in the video 
 *  to make the resulting image more clean for 
 *  both black and white backgrounds.
 *  
 *  Have fun!
 */
public class GrayscaleBlend
{
	private final BufferedImage imgW;
	private final BufferedImage imgB;
	private final File output;
	private final boolean resize;
	private final int width, height;
	
	public GrayscaleBlend(String inpath1, String inpath2, String outpath, String resize) throws IOException, IllegalArgumentException
	{
		if(inpath1 == null)
		{
			throw new IllegalArgumentException("Please specify the input filepath for white background.");
		}
		if(inpath2 == null)
		{
			throw new IllegalArgumentException("Please specify the input filepath for black background.");
		}
		if(outpath == null)
		{
			throw new IllegalArgumentException("Please specify the output filepath.");
		}
		
		this.imgW = ImageIO.read(new File(inpath1));
		this.imgB = ImageIO.read(new File(inpath2));
		this.output = new File(outpath);
		String[] cache = resize.split("x", 2);
		try
		{
			this.width = Integer.parseInt(cache[0]);
			this.height = Integer.parseInt(cache[1]);
		}
		catch(NumberFormatException nfe)
		{
			throw new IllegalArgumentException("Invalid resolution specifier: \"" + resize + "\".");
		}
		
		if(this.width <= 0 || this.height <= 0)
		{
			throw new IllegalArgumentException("Resolution width/height may ONLY be positive integers!");
		}
		this.resize = true;
	}
	
	public GrayscaleBlend(String inpath1, String inpath2, String outpath) throws IOException
	{
		if(inpath1 == null)
		{
			throw new IllegalArgumentException("Please specify the input filepath for white background.");
		}
		if(inpath2 == null)
		{
			throw new IllegalArgumentException("Please specify the input filepath for black background.");
		}
		if(outpath == null)
		{
			throw new IllegalArgumentException("Please specify the output filepath.");
		}
		
		this.imgW = ImageIO.read(new File(inpath1));
		this.imgB = ImageIO.read(new File(inpath2));
		this.output = new File(outpath);
		if(this.imgW.getWidth() != this.imgB.getWidth() || this.imgW.getHeight() != this.imgB.getHeight())
		{
			throw new IllegalArgumentException("Resolution mismatch! Please specify --res parameter.");
		}
		this.width = this.imgW.getWidth(); 
		this.height = this.imgW.getHeight();
		this.resize = false;
	}
	
	public void convert() throws IOException
	{
		BufferedImage cache1 = this.imgW;
		BufferedImage cache2 = this.imgB;
		if(this.resize)
		{
			cache1 = resize(cache1, this.width, this.height);
			cache2 = resize(cache1, this.width, this.height);
		}
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		int gray, alpha, pixel1, pixel2;
		for(int i = 0 ; i < this.width ; i++)
		{
			for(int j = 0 ; j < this.height ; j++)
			{
				/*
				 * calculate offset pixel brightness for image using white background
				 */
				pixel1 = cache1.getRGB(i, j);
				// Convert RGB to grayscale using linear luminance in CIE 1931 colorspace.
				pixel1 = (int)(((pixel1 & 0xFF) * 0.0722 + ((pixel1 >> 8) & 0xFF) * 0.7152 + ((pixel1 >> 16) & 0xFF) * 0.2126) / 2 + 0.5 + 0x7F) & 0xFF;
				
				/*
				 * calculate offset pixel brightness for image using black background
				 */
				pixel2 = cache2.getRGB(i, j);
				// Convert RGB to grayscale using linear luminance in CIE 1931 colorspace.
				pixel2 = (int)(((pixel2 & 0xFF) * 0.0722 + ((pixel2 >> 8) & 0xFF) * 0.7152 + ((pixel2 >> 16) & 0xFF) * 0.2126) / 2 + 0.5) & 0xFF;
				
				alpha = (0xFF + pixel2 - pixel1) & 0xFF;
				gray = (int)(pixel2 * 255. / alpha + .5) & 0xFF;
				result.setRGB(i, j, gray | gray << 8 | gray << 16 | alpha << 24);
			}
		}
		
		ImageIO.write(result, "PNG", this.output);
	}
	
	/**
	 * Solution provided by Ocracoke on StackOverflow:
	 *  https://stackoverflow.com/questions/9417356/bufferedimage-resize
	 */
	private static BufferedImage resize(BufferedImage img, int width, int height)
	{ 
	    Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	    BufferedImage ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = ret.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return ret;
	}
	
	public static void main(String[] args) throws Exception
	{
		final HashMap<String, String> params = new HashMap<>();
		
		if(args.length == 1 && args[0].equalsIgnoreCase("--help"))
		{
			String jar_name = "grayscaleblend.jar";
			try
			{
				new File(GrayscaleBlend.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
			}
			catch(Exception e) {}
			System.out.println("#--------------------------------------------#");
			System.out.println("|   -=+[ Grayscale Blend by tan2pow16 ]+=-   |");
			System.out.println("|      < https://github.com/tan2pow16 >      |");
			System.out.println("#--------------------------------------------#");
			System.out.println();
			System.out.println(" Available flags:");
			System.out.println("   --in1 <input filepath for white background>");
			System.out.println("   --in2 <input filepath for black background>");
			System.out.println("   --out <output filepath to store the result>");
			System.out.println("   --res <width>x<height> (OPTIONAL for inputs");
			System.out.println("           with same resolution.)");
			System.out.println();
			System.out.println(" Example:");
			System.out.println("  java.exe -jar " + jar_name + " --in1 img01.png --in2 img02.png --out output.png --res 1366x768");
			System.out.println();
			return;
		}
		
		for(int i = 0 ; i < args.length ; i++)
		{
			if(params.containsKey(args[i]))
			{
				throw new IllegalArgumentException("Duplicated parameters: \"" + args[i] + "\".");
			}
			
			if(args.length - i == 1)
			{
				throw new IllegalArgumentException("Blank parameter: \"" + args[i] + "\".");
			}
			
			if(args[i].startsWith("--"))
			{
				params.put(args[i].toLowerCase(), args[++i]);
			}
			else
			{
				throw new IllegalArgumentException("Invalid parameter: \"" + args[i] + "\".");
			}
		}
		
		GrayscaleBlend instance;
		if(params.containsKey("--res"))
		{
			instance = new GrayscaleBlend(params.get("--in1"), params.get("--in2"), params.get("--out"), params.get("--res"));
		}
		else
		{
			instance = new GrayscaleBlend(params.get("--in1"), params.get("--in2"), params.get("--out"));
		}
		instance.convert();
	}
}
