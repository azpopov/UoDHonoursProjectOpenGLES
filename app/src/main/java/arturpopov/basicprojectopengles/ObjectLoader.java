package arturpopov.basicprojectopengles;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

/**
 * Created by arturpopov on 09/02/2017.
 */

@SuppressWarnings("PointlessArithmeticExpression")
class ObjectLoader
{
    public static final int VERTEX_ARRAY_INDEX = 0;
    public static final int TEXTURE_COORDINATE_ARRAY_INDEX = 1;
    public static final int NORMAL_ARRAY_INDEX = 2;
    public static final int TANGENT_ARRAY_INDEX = 3;
    public static final int BITANGENT_ARRAY_INDEX = 4;
    private static final int INDICE_ARRAY_INDEX = 5;


    static ArrayList<ArrayList<Float>> loadObjFile(String fileName, Context context)
    {
        if (context == null)
        {
            Log.d(LogTag.CONTEXT, "Context null");
            throw new RuntimeException("Context Null");
        }

        ArrayList<ArrayList<Float>> resultList = new ArrayList<>();
        ArrayList<Float> vectorDataTemp = new ArrayList<>();
        ArrayList<Float> textureCoordTemp = new ArrayList<>();
        ArrayList<Float> normalDataTemp = new ArrayList<>();

        ArrayList<Float> vectorDataFinal = new ArrayList<>();
        ArrayList<Float> textureCoordFinal = new ArrayList<>();
        ArrayList<Float> normalDataFinal = new ArrayList<>();
        ArrayList<Float> tangentsDataFinal = new ArrayList<>();
        ArrayList<Float> biTangentsDataFinal = new ArrayList<>();

        String everything = FileReader.readFile("objFiles/" + fileName, context);
        String[] splitEverything = everything.split("\n");

        for (String aSplitEverything : splitEverything) {
            if (Objects.equals(aSplitEverything.substring(0, 2), "v ")) {
                String stringBuffer = aSplitEverything.substring(2);
                String[] split = stringBuffer.split("\\s+");
                Float[] v = new Float[3];
                v[0] = Float.parseFloat(split[0]);
                v[1] = Float.parseFloat(split[1]);
                v[2] = Float.parseFloat(split[2]);
                Collections.addAll(vectorDataTemp, v);
            } else if (Objects.equals(aSplitEverything.substring(0, 2), "vt")) {
                String stringBuffer = aSplitEverything.substring(3);
                String[] split = stringBuffer.split("\\s+");
                Float[] textureCoordsArray = new Float[2];
                textureCoordsArray[0] = Float.parseFloat(split[0]);
                textureCoordsArray[1] = Float.parseFloat(split[1]);
                Collections.addAll(textureCoordTemp, textureCoordsArray);
            } else if (Objects.equals(aSplitEverything.substring(0, 2), "vn")) {
                String stringBuffer = aSplitEverything.substring(3);
                String[] split = stringBuffer.split("\\s+");
                Float[] normalArray = new Float[3];
                normalArray[0] = Float.parseFloat(split[0]);
                normalArray[1] = Float.parseFloat(split[1]);
                normalArray[2] = Float.parseFloat(split[2]);
                Collections.addAll(normalDataTemp, normalArray);
            } else if (Objects.equals(aSplitEverything.substring(0, 2), "f ")) {
                MainRenderer.polygonCounter += 3;
                String stringBuffer = aSplitEverything.substring(2);
                stringBuffer.replaceAll("\\//", " ");

                String[] firstSplit = stringBuffer.split("\\s+");
                String[] finalSplit = new String[9];
                int index = 0;
                for (String s : firstSplit) {
                    String[] split = s.split("/");
                    finalSplit[index++] = split[0];
                    finalSplit[index++] = split[1];
                    finalSplit[index++] = split[2];
                }
                Integer[] faceValues = new Integer[9];
                for (int i = 0; i < faceValues.length; i++) {
                    faceValues[i] = Integer.parseInt(finalSplit[i]) - 1;
                }
                vectorDataFinal.add(vectorDataTemp.get(faceValues[0] * 3));
                vectorDataFinal.add(vectorDataTemp.get((faceValues[0] * 3) + 1));
                vectorDataFinal.add(vectorDataTemp.get((faceValues[0] * 3) + 2));

                textureCoordFinal.add(textureCoordTemp.get(faceValues[1] * 2));
                textureCoordFinal.add(textureCoordTemp.get((faceValues[1] * 2) + 1));

                normalDataFinal.add(normalDataTemp.get((faceValues[2] * 3) + 0));
                normalDataFinal.add(normalDataTemp.get((faceValues[2] * 3) + 1));
                normalDataFinal.add(normalDataTemp.get((faceValues[2] * 3) + 2));

                vectorDataFinal.add(vectorDataTemp.get((faceValues[3] * 3) + 0));
                vectorDataFinal.add(vectorDataTemp.get((faceValues[3] * 3) + 1));
                vectorDataFinal.add(vectorDataTemp.get((faceValues[3] * 3) + 2));

                textureCoordFinal.add(textureCoordTemp.get((faceValues[4] * 2) + 0));
                textureCoordFinal.add(textureCoordTemp.get((faceValues[4] * 2) + 1));

                normalDataFinal.add(normalDataTemp.get((faceValues[5] * 3) + 0));
                normalDataFinal.add(normalDataTemp.get((faceValues[5] * 3) + 1));
                normalDataFinal.add(normalDataTemp.get((faceValues[5] * 3) + 2));

                vectorDataFinal.add(vectorDataTemp.get((faceValues[6] * 3) + 0));
                vectorDataFinal.add(vectorDataTemp.get((faceValues[6] * 3) + 1));
                vectorDataFinal.add(vectorDataTemp.get((faceValues[6] * 3) + 2));

                textureCoordFinal.add(textureCoordTemp.get((faceValues[7] * 2) + 0));
                textureCoordFinal.add(textureCoordTemp.get((faceValues[7] * 2) + 1));

                normalDataFinal.add(normalDataTemp.get((faceValues[8] * 3) + 0));
                normalDataFinal.add(normalDataTemp.get((faceValues[8] * 3) + 1));
                normalDataFinal.add(normalDataTemp.get((faceValues[8] * 3) + 2));

            }
        }

            for (int i = 0; i < vectorDataFinal.size()/3; i += 3)
            {
                Float[] v0 = {
                        vectorDataFinal.get((i * 3) + 0),
                        vectorDataFinal.get((i * 3) + 1),
                        vectorDataFinal.get((i * 3) + 2)
                };
                Float[] v1 = {
                        vectorDataFinal.get(((i + 1) * 3) + 0),
                        vectorDataFinal.get(((i + 1) * 3) + 1),
                        vectorDataFinal.get(((i + 1) * 3) + 2)
                };
                Float[] v2 = {
                        vectorDataFinal.get(((i + 2) * 3) + 0),
                        vectorDataFinal.get(((i + 2) * 3) + 1),
                        vectorDataFinal.get(((i + 2) * 3) + 2)
                };

                Float[] uv0 = {
                        textureCoordFinal.get((i * 2)),
                        textureCoordFinal.get((i * 2) + 1)
                };
                Float[] uv1 = {
                        textureCoordFinal.get(((i + 1) * 2) + 0),
                        textureCoordFinal.get(((i + 1) * 2) + 1)
                };
                Float[] uv2 = {
                        textureCoordFinal.get(((i + 2) * 2) + 0),
                        textureCoordFinal.get(((i + 2) * 2) + 1)
                };

                Float[] deltaPos1 = {v1[0] - v0[0], v1[1] - v0[1], v1[2] - v0[2]};
                Float[] deltaPos2 = {v2[0] - v0[0], v2[1] - v0[1], v2[2] - v0[2]};

                Float[] deltaUV1 = {uv1[0] - uv0[0], uv1[1] - uv0[1]};
                Float[] deltaUV2 = {uv2[0] - uv0[0], uv2[1] - uv0[1]};

                float r = 1.0f / (deltaUV1[0] * deltaUV2[1] - deltaUV1[1] * deltaUV2[0]);
                Float[] tangent = {
                        (deltaPos1[0] * deltaUV2[1] - deltaPos2[0] * deltaUV1[1]) * r,
                        (deltaPos1[1] * deltaUV2[1] - deltaPos2[1] * deltaUV1[1]) * r,
                        (deltaPos1[2] * deltaUV2[1] - deltaPos2[2] * deltaUV1[1]) * r,
                };
                Float[] biTangent = {
                        (deltaPos2[0] * deltaUV1[0] - deltaPos1[0] * deltaUV2[0]) * r,
                        (deltaPos2[1] * deltaUV1[0] - deltaPos1[1] * deltaUV2[0]) * r,
                        (deltaPos2[2] * deltaUV1[0] - deltaPos1[2] * deltaUV2[0]) * r,
                };

                Collections.addAll(tangentsDataFinal, tangent);
                Collections.addAll(tangentsDataFinal, tangent);
                Collections.addAll(tangentsDataFinal, tangent);

                Collections.addAll(biTangentsDataFinal, biTangent);
                Collections.addAll(biTangentsDataFinal, biTangent);
                Collections.addAll(biTangentsDataFinal, biTangent);
            }




        resultList.add(vectorDataFinal);
        resultList.add(textureCoordFinal);
        resultList.add(normalDataFinal);
        resultList.add(tangentsDataFinal);
        resultList.add(biTangentsDataFinal);
        return resultList;
    }


    public static ArrayList<ArrayList<Float>> IndexObject(ArrayList<ArrayList<Float>> in)
    {
        ArrayList<ArrayList<Float>> result = new ArrayList<>();
        result.add(new ArrayList<Float>());
        result.add(new ArrayList<Float>());
        result.add(new ArrayList<Float>());
        result.add(new ArrayList<Float>());
        result.add(new ArrayList<Float>());
        result.add(new ArrayList<Float>());


        for (int i = 0; i < in.get(VERTEX_ARRAY_INDEX).size()/3; i++)
        {
            int index = -1;
            {
                Float[] comparingVertex = new Float[]
                        {
                                in.get(VERTEX_ARRAY_INDEX).get((i * 3) + 0),
                                in.get(VERTEX_ARRAY_INDEX).get((i * 3) + 1),
                                in.get(VERTEX_ARRAY_INDEX).get((i * 3) + 2)
                        };

                Float[] comparingUV = new Float[]
                        {
                                in.get(TEXTURE_COORDINATE_ARRAY_INDEX).get((i * 2) + 0), in.get(TEXTURE_COORDINATE_ARRAY_INDEX).get((i * 2) + 1)
                        };
                Float[] comparingNormal = new Float[]
                        {
                                in.get(NORMAL_ARRAY_INDEX).get((i * 3) + 0),
                                in.get(NORMAL_ARRAY_INDEX).get((i * 3) + 1),
                                in.get(NORMAL_ARRAY_INDEX).get((i * 3) + 2)
                        };

                for (int j = 0; j < result.get(VERTEX_ARRAY_INDEX).size()/3; j++)
                {
                    if(isNear(comparingVertex[0], result.get(VERTEX_ARRAY_INDEX).get((j * 3) + 0)) &&
                                    isNear(comparingVertex[1], result.get(VERTEX_ARRAY_INDEX).get((j * 3) + 1)) &&
                                    isNear(comparingVertex[2], result.get(VERTEX_ARRAY_INDEX).get((j * 3) + 2)) &&
                                    isNear(comparingUV[0], result.get(TEXTURE_COORDINATE_ARRAY_INDEX).get((j * 2) + 0)) &&
                                    isNear(comparingUV[1], result.get(TEXTURE_COORDINATE_ARRAY_INDEX).get((j * 2) + 1)) &&
                                    isNear(comparingNormal[0], result.get(NORMAL_ARRAY_INDEX).get((j * 3) + 0)) &&
                                    isNear(comparingNormal[1], result.get(NORMAL_ARRAY_INDEX).get((j * 3) + 1)) &&
                                    isNear(comparingNormal[2], result.get(NORMAL_ARRAY_INDEX).get((j * 3) + 2)))
                    {
                        index = j;
                        break;
                    }
                }


            }
            if(index > -1)
            {
                result.get(INDICE_ARRAY_INDEX).add((float)index);
                Float[] newTangent = {
                        result.get(TANGENT_ARRAY_INDEX).get((index * 3) + 0) + in.get(TANGENT_ARRAY_INDEX).get((index * 3) + 0),
                        result.get(TANGENT_ARRAY_INDEX).get((index * 3) + 1) + in.get(TANGENT_ARRAY_INDEX).get((index * 3) + 1),
                        result.get(TANGENT_ARRAY_INDEX).get((index * 3) + 2) + in.get(TANGENT_ARRAY_INDEX).get((index * 3) + 2),
                };
                Collections.addAll(result.get(TANGENT_ARRAY_INDEX),newTangent);
                Float[] newBiTangent = {
                        result.get(BITANGENT_ARRAY_INDEX).get((index * 3) + 0) + in.get(BITANGENT_ARRAY_INDEX).get((index * 3) + 0),
                        result.get(BITANGENT_ARRAY_INDEX).get((index * 3) + 1) + in.get(BITANGENT_ARRAY_INDEX).get((index * 3) + 1),
                        result.get(BITANGENT_ARRAY_INDEX).get((index * 3) + 2) + in.get(BITANGENT_ARRAY_INDEX).get((index * 3) + 2),
                };
                Collections.addAll(result.get(BITANGENT_ARRAY_INDEX),newBiTangent);

            }
            else
            {
                Collections.addAll(result.get(VERTEX_ARRAY_INDEX), in.get(VERTEX_ARRAY_INDEX).get((i * 3) + 0),
                        in.get(VERTEX_ARRAY_INDEX).get((i * 3) + 1),
                        in.get(VERTEX_ARRAY_INDEX).get((i * 3) + 2));
                Collections.addAll(result.get(TEXTURE_COORDINATE_ARRAY_INDEX), in.get(TEXTURE_COORDINATE_ARRAY_INDEX).get((i * 2) + 0),
                        in.get(TEXTURE_COORDINATE_ARRAY_INDEX).get((i * 2) + 1));
                Collections.addAll(result.get(NORMAL_ARRAY_INDEX), in.get(NORMAL_ARRAY_INDEX).get((i * 3) + 0),
                        in.get(NORMAL_ARRAY_INDEX).get((i * 3) + 1),
                        in.get(NORMAL_ARRAY_INDEX).get((i * 3) + 2));
                Collections.addAll(result.get(NORMAL_ARRAY_INDEX), in.get(NORMAL_ARRAY_INDEX).get((i * 3) + 0),
                        in.get(NORMAL_ARRAY_INDEX).get((i * 3) + 1),
                        in.get(NORMAL_ARRAY_INDEX).get((i * 3) + 2));
                Collections.addAll(result.get(TANGENT_ARRAY_INDEX), in.get(TANGENT_ARRAY_INDEX).get((i * 3) + 0),
                        in.get(TANGENT_ARRAY_INDEX).get((i * 3) + 1),
                        in.get(TANGENT_ARRAY_INDEX).get((i * 3) + 2));
                Collections.addAll(result.get(BITANGENT_ARRAY_INDEX), in.get(BITANGENT_ARRAY_INDEX).get((i * 3) + 0),
                        in.get(BITANGENT_ARRAY_INDEX).get((i * 3) + 1),
                        in.get(BITANGENT_ARRAY_INDEX).get((i * 3) + 2));
                Collections.addAll(result.get(INDICE_ARRAY_INDEX), (float)result.get(INDICE_ARRAY_INDEX).size());
            }
        }
        return result;
    }

   private static boolean isNear(float lhs, float rhs)
   {
       return Math.abs( lhs-rhs ) < 0.01f;
   }

   static ArrayList<ArrayList<Float>> loadObjDefault(String fileName, Context context)
   {
       if (context == null)
       {
           Log.d(LogTag.CONTEXT, "Context null");
           throw new RuntimeException("Context Null");
       }

       ArrayList<ArrayList<Float>> resultList = new ArrayList<>();
       ArrayList<Float> vectorDataTemp = new ArrayList<>();
       ArrayList<Float> textureCoordTemp = new ArrayList<>();
       ArrayList<Float> normalDataTemp = new ArrayList<>();

       ArrayList<Float> vectorDataFinal = new ArrayList<>();
       ArrayList<Float> textureCoordFinal = new ArrayList<>();
       ArrayList<Float> normalDataFinal = new ArrayList<>();

       String everything = FileReader.readFile("objFiles/" + fileName, context);
       String[] splitEverything = everything.split("\n");

       for (String aSplitEverything : splitEverything) {
           if (Objects.equals(aSplitEverything.substring(0, 2), "v ")) {
               String stringBuffer = aSplitEverything.substring(2);
               String[] split = stringBuffer.split("\\s+");
               Float[] v = new Float[3];
               v[0] = Float.parseFloat(split[0]);
               v[1] = Float.parseFloat(split[1]);
               v[2] = Float.parseFloat(split[2]);
               Collections.addAll(vectorDataTemp, v);
           } else if (Objects.equals(aSplitEverything.substring(0, 2), "vt")) {
               String stringBuffer = aSplitEverything.substring(3);
               String[] split = stringBuffer.split("\\s+");
               Float[] textureCoordsArray = new Float[2];
               textureCoordsArray[0] = Float.parseFloat(split[0]);
               textureCoordsArray[1] = Float.parseFloat(split[1]);
               Collections.addAll(textureCoordTemp, textureCoordsArray);
           } else if (Objects.equals(aSplitEverything.substring(0, 2), "vn")) {
               String stringBuffer = aSplitEverything.substring(3);
               String[] split = stringBuffer.split("\\s+");
               Float[] normalArray = new Float[3];
               normalArray[0] = Float.parseFloat(split[0]);
               normalArray[1] = Float.parseFloat(split[1]);
               normalArray[2] = Float.parseFloat(split[2]);
               Collections.addAll(normalDataTemp, normalArray);
           } else if (Objects.equals(aSplitEverything.substring(0, 2), "f ")) {
               String stringBuffer = aSplitEverything.substring(2);
               stringBuffer.replaceAll("\\//", " ");

               String[] firstSplit = stringBuffer.split("\\s+");
               String[] finalSplit = new String[9];
               int index = 0;
               for (String s : firstSplit) {
                   String[] split = s.split("/");
                   finalSplit[index++] = split[0];
                   finalSplit[index++] = split[1];
                   finalSplit[index++] = split[2];
               }
               Integer[] faceValues = new Integer[9];
               for (int i = 0; i < faceValues.length; i++) {
                   faceValues[i] = Integer.parseInt(finalSplit[i]) - 1;
               }
               vectorDataFinal.add(vectorDataTemp.get(faceValues[0] * 3));
               vectorDataFinal.add(vectorDataTemp.get((faceValues[0] * 3) + 1));
               vectorDataFinal.add(vectorDataTemp.get((faceValues[0] * 3) + 2));

               textureCoordFinal.add(textureCoordTemp.get(faceValues[1] * 2));
               textureCoordFinal.add(textureCoordTemp.get((faceValues[1] * 2) + 1));

               normalDataFinal.add(normalDataTemp.get((faceValues[2] * 3) + 0));
               normalDataFinal.add(normalDataTemp.get((faceValues[2] * 3) + 1));
               normalDataFinal.add(normalDataTemp.get((faceValues[2] * 3) + 2));

               vectorDataFinal.add(vectorDataTemp.get((faceValues[3] * 3) + 0));
               vectorDataFinal.add(vectorDataTemp.get((faceValues[3] * 3) + 1));
               vectorDataFinal.add(vectorDataTemp.get((faceValues[3] * 3) + 2));

               textureCoordFinal.add(textureCoordTemp.get((faceValues[4] * 2) + 0));
               textureCoordFinal.add(textureCoordTemp.get((faceValues[4] * 2) + 1));

               normalDataFinal.add(normalDataTemp.get((faceValues[5] * 3) + 0));
               normalDataFinal.add(normalDataTemp.get((faceValues[5] * 3) + 1));
               normalDataFinal.add(normalDataTemp.get((faceValues[5] * 3) + 2));

               vectorDataFinal.add(vectorDataTemp.get((faceValues[6] * 3) + 0));
               vectorDataFinal.add(vectorDataTemp.get((faceValues[6] * 3) + 1));
               vectorDataFinal.add(vectorDataTemp.get((faceValues[6] * 3) + 2));

               textureCoordFinal.add(textureCoordTemp.get((faceValues[7] * 2) + 0));
               textureCoordFinal.add(textureCoordTemp.get((faceValues[7] * 2) + 1));

               normalDataFinal.add(normalDataTemp.get((faceValues[8] * 3) + 0));
               normalDataFinal.add(normalDataTemp.get((faceValues[8] * 3) + 1));
               normalDataFinal.add(normalDataTemp.get((faceValues[8] * 3) + 2));
           }
       }

       resultList.add(vectorDataFinal);
       resultList.add(textureCoordFinal);
       resultList.add(normalDataFinal);
       return resultList;
   }

    static ArrayList<ArrayList<Float>> loadUnityTerrain(String fileName, Context context)
    {
        if (context == null)
        {
            Log.d(LogTag.CONTEXT, "Context null");
            throw new RuntimeException("Context Null");
        }

        ArrayList<ArrayList<Float>> resultList = new ArrayList<>();
        ArrayList<Float> vectorDataTemp = new ArrayList<>();
        ArrayList<Float> textureCoordTemp = new ArrayList<>();

        ArrayList<Float> vectorDataFinal = new ArrayList<>();
        ArrayList<Float> textureCoordFinal = new ArrayList<>();
        ArrayList<Float> normalDataFinal = new ArrayList<>();

        String everything = FileReader.readFile("objFiles/" + fileName, context);
        String[] splitEverything = everything.split("\n");

        for (String aSplitEverything : splitEverything) {
            if (Objects.equals(aSplitEverything.substring(0, 2), "v ")) {
                String stringBuffer = aSplitEverything.substring(2);
                String[] split = stringBuffer.split("\\s+");
                Float[] v = new Float[3];
                v[0] = Float.parseFloat(split[0]);
                v[1] = Float.parseFloat(split[1]);
                v[2] = Float.parseFloat(split[2]);
                Collections.addAll(vectorDataTemp, v);
            } else if (Objects.equals(aSplitEverything.substring(0, 2), "vt")) {
                String stringBuffer = aSplitEverything.substring(3);
                String[] split = stringBuffer.split("\\s+");
                Float[] textureCoordsArray = new Float[2];
                textureCoordsArray[0] = Float.parseFloat(split[0]);
                textureCoordsArray[1] = Float.parseFloat(split[1]);
                Collections.addAll(textureCoordTemp, textureCoordsArray);
            } else if (Objects.equals(aSplitEverything.substring(0, 2), "f ")) {
                String stringBuffer = aSplitEverything.substring(2);
                stringBuffer.replaceAll("\\//", " ");

                String[] firstSplit = stringBuffer.split("\\s+");
                String[] finalSplit = new String[6];
                int index = 0;
                for (String s : firstSplit) {
                    String[] split = s.split("/");
                    finalSplit[index++] = split[0];
                    finalSplit[index++] = split[1];
                }
                Integer[] faceValues = new Integer[6];
                for (int i = 0; i < faceValues.length; i++) {
                    faceValues[i] = Integer.parseInt(finalSplit[i]) - 1;
                }
                vectorDataFinal.add(vectorDataTemp.get(faceValues[0] * 3));
                vectorDataFinal.add(vectorDataTemp.get((faceValues[0] * 3) + 1));
                vectorDataFinal.add(vectorDataTemp.get((faceValues[0] * 3) + 2));

                textureCoordFinal.add(textureCoordTemp.get(faceValues[1] * 2));
                textureCoordFinal.add(textureCoordTemp.get((faceValues[1] * 2) + 1));


                vectorDataFinal.add(vectorDataTemp.get((faceValues[2] * 3) + 0));
                vectorDataFinal.add(vectorDataTemp.get((faceValues[2] * 3) + 1));
                vectorDataFinal.add(vectorDataTemp.get((faceValues[2] * 3) + 2));

                textureCoordFinal.add(textureCoordTemp.get((faceValues[3] * 2) + 0));
                textureCoordFinal.add(textureCoordTemp.get((faceValues[3] * 2) + 1));

                vectorDataFinal.add(vectorDataTemp.get((faceValues[4] * 3) + 0));
                vectorDataFinal.add(vectorDataTemp.get((faceValues[4] * 3) + 1));
                vectorDataFinal.add(vectorDataTemp.get((faceValues[4] * 3) + 2));

                textureCoordFinal.add(textureCoordTemp.get((faceValues[5] * 2) + 0));
                textureCoordFinal.add(textureCoordTemp.get((faceValues[5] * 2) + 1));


            }
        }

        for(int i = 0; i < vectorDataFinal.size() - 6; i += 3)
        {
            float[] normal = calculateSurfaceNormal(new float[]
                    {
                            vectorDataFinal.get(i), vectorDataFinal.get(i+1), vectorDataFinal.get(i+2),
                            vectorDataFinal.get(i+3), vectorDataFinal.get(i+4), vectorDataFinal.get(i+5),
                            vectorDataFinal.get(i+6), vectorDataFinal.get(i+7), vectorDataFinal.get(i+8)
                    });
            normalDataFinal.add(normal[0]);
            normalDataFinal.add(normal[1]);
            normalDataFinal.add(normal[2]);
        }
        normalDataFinal.add(normalDataFinal.get(0));
        normalDataFinal.add(normalDataFinal.get(1));
        normalDataFinal.add(normalDataFinal.get(2));
        normalDataFinal.add(normalDataFinal.get(3));
        normalDataFinal.add(normalDataFinal.get(4));
        normalDataFinal.add(normalDataFinal.get(5));

        resultList.add(vectorDataFinal);
        resultList.add(textureCoordFinal);
        resultList.add(normalDataFinal);
        return resultList;
    }



   private static float[] calculateSurfaceNormal(float[] triangle)
   {
        float[] normal = new float[]{0.f, 0.f, 0.f};

       float[] vectorU = {triangle[3] - triangle[0], triangle[4] - triangle[1],triangle[5] - triangle[2]};
       float[] vectorV = {triangle[6] - triangle[0], triangle[7] - triangle[1],triangle[8] - triangle[2]};

       normal[0] = (vectorU[1] * vectorV[2]) - (vectorU[2] * vectorV[1]);
       normal[1] = (vectorU[2] * vectorV[0]) - (vectorU[0] * vectorV[2]);
       normal[2] = (vectorU[0] * vectorV[1]) - (vectorU[1] * vectorV[0]);

       return normal;
   }
}


