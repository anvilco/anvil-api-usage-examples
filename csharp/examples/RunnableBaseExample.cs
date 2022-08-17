namespace AnvilExamples;

public interface IBaseExample
{
    Task Run(string apiKey);
}

abstract class RunnableBaseExample : IBaseExample
{
    protected string _apiKey;

    public RunnableBaseExample(string apiKey)
    {
        _apiKey = apiKey;
    }

    public virtual Task Run(string apiKey)
    {
        throw new NotImplementedException();
    }

    public virtual Task Run(string apiKey, string otherArg)
    {
        throw new NotImplementedException();
    }
}