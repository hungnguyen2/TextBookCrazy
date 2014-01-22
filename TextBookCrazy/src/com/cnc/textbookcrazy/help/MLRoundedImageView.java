package com.cnc.textbookcrazy.help;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.cnc.textbookcrazy.R;

public class MLRoundedImageView extends ImageView
{
	
	public static int	color;
	
	public static int	size_border;
	
	public static Bitmap getCroppedBitmap( Bitmap bmp, int radius )
	{
		Bitmap sbmp;
		if ( bmp.getWidth( ) != radius || bmp.getHeight( ) != radius )
		{
			float smallest = Math.min( bmp.getWidth( ), bmp.getHeight( ) );
			float factor = smallest / radius;
			sbmp = Bitmap.createScaledBitmap( bmp, ( int ) ( bmp.getWidth( ) / factor ),
					( int ) ( bmp.getHeight( ) / factor ), false );
		}
		else
		{
			sbmp = bmp;
		}
		Bitmap output = Bitmap.createBitmap( radius, radius,
				Config.ARGB_8888 );
		Canvas canvas = new Canvas( output );
		
		// final int color = 0xffa19774;
		final Paint paint = new Paint( );
		final Rect rect = new Rect( 0, 0, radius, radius );
		
		paint.setAntiAlias( true );
		paint.setFilterBitmap( true );
		paint.setDither( true );
		canvas.drawARGB( 0, 0, 0, 0 );
		paint.setColor( color );
		Log.d( "size_border", size_border + "" );
		canvas.drawCircle( radius / 2,
				radius / 2, radius / 2 - size_border, paint );
		paint.setXfermode( new PorterDuffXfermode( Mode.SRC_IN ) );
		canvas.drawBitmap( sbmp, rect, rect, paint );
		return output;
	}
	
	public MLRoundedImageView( Context context )
	{
		super( context );
	}
	
	public MLRoundedImageView( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		init( attrs );
	}
	
	public MLRoundedImageView( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );
		init( attrs );
	}
	
	@SuppressLint( "Recycle" )
	private void init( AttributeSet attrs )
	{
		TypedArray a = getContext( ).obtainStyledAttributes( attrs, R.styleable.Color_CustomImageView );
		color = a.getColor( R.styleable.Color_CustomImageView_color, 0x00000000 );
		size_border = a.getInt( R.styleable.Color_CustomImageView_size_border, 6 );
	}
	
	@SuppressLint( "DrawAllocation" )
	@Override
	protected void onDraw( Canvas canvas )
	{
		
		Drawable drawable = getDrawable( );
		
		if ( drawable == null )
		{
			return;
		}
		
		if ( getWidth( ) == 0 || getHeight( ) == 0 )
		{
			return;
		}
		Bitmap b = ( ( BitmapDrawable ) drawable ).getBitmap( );
		Bitmap bitmap = b.copy( Bitmap.Config.ARGB_8888, true );
		
		int w = getWidth( ), h = getHeight( );
		
		Bitmap roundBitmap = getCroppedBitmap( bitmap, w );
		
		Paint paint = new Paint( );
		paint.setAntiAlias( true );
		paint.setFilterBitmap( true );
		paint.setDither( true );
		paint.setColor( color );
		// paint.setXfermode( new PorterDuffXfermode( Mode.SRC_IN ) );
		canvas.drawCircle( w / 2, w / 2, w / 2, paint );
		
		canvas.drawBitmap( roundBitmap, 0, 0, null );
		
	}
	
	@Override
	public void setBackgroundColor( int color )
	{
		super.setBackgroundColor( color );
	}
	
}
